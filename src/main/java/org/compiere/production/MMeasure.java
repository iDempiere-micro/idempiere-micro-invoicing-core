package org.compiere.production;

import kotliquery.Row;
import org.compiere.crm.MUser;
import org.compiere.crm.MUserKt;
import org.compiere.model.I_PA_Measure;
import org.compiere.model.MeasureInterface;
import org.compiere.model.Rule;
import org.compiere.orm.MRole;
import org.compiere.orm.MRoleKt;
import org.compiere.orm.MTable;
import org.compiere.orm.PO;
import org.compiere.orm.TimeUtil;
import org.compiere.rule.MRule;
import org.compiere.util.MsgKt;
import org.idempiere.common.util.CCache;
import org.idempiere.common.util.Env;
import org.idempiere.common.util.Util;

import javax.script.ScriptEngine;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.StringTokenizer;
import java.util.logging.Level;

import static software.hsharp.core.util.DBKt.getSQLValueBD;

/**
 * Performance Measure
 *
 * @author Jorg Janke
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * <li>BF [ 1887674 ] Deadlock when try to modify PA Goal's Measure Target
 * @author victor.perez@e-evolution.com, www.e-evolution.com
 * <li>FR [ 2905227 ] Calculate Measure based on the script to PA
 * <li>https://sourceforge.net/tracker/?func=detail&aid=2905227&group_id=176962&atid=879335
 * @version $Id: MMeasure.java,v 1.2 2006/07/30 00:51:05 jjanke Exp $
 */
public class MMeasure extends X_PA_Measure {
    /**
     *
     */
    private static final long serialVersionUID = 6274990637485210675L;
    /**
     * Cache
     */
    private static CCache<Integer, MMeasure> s_cache =
            new CCache<>(I_PA_Measure.Table_Name, 10);

    /**
     * Standard Constructor
     *
     * @param PA_Measure_ID id
     */
    public MMeasure(int PA_Measure_ID) {
        super(PA_Measure_ID);
    } //	MMeasure

    /**
     * Load Constructor
     */
    public MMeasure(Row row) {
        super(row);
    } //	MMeasure

    /**
     * Get MMeasure from Cache
     *
     * @param PA_Measure_ID id
     * @return MMeasure
     */
    public static MMeasure get(int PA_Measure_ID) {
        Integer key = PA_Measure_ID;
        MMeasure retValue = s_cache.get(key);
        if (retValue != null) return retValue;
        retValue = new MMeasure(PA_Measure_ID);
        if (retValue.getId() != 0) s_cache.put(key, retValue);
        return retValue;
    } //	get

    /**
     * String Representation
     *
     * @return info
     */
    public String toString() {
        return "MMeasure[" + getId() + "-" + getName() + "]";
    } //	toString

    /**
     * Before Save
     *
     * @param newRecord new
     * @return true
     */
    protected boolean beforeSave(boolean newRecord) {
        if (X_PA_Measure.MEASURETYPE_Calculated.equals(getMeasureType())
                && getMeasureCalcId() == 0) {
            log.saveError("FillMandatory", MsgKt.getElementTranslation("PA_MeasureCalc_ID"));
            return false;
        } else if (X_PA_Measure.MEASURETYPE_Ratio.equals(getMeasureType()) && getRatioId() == 0) {
            log.saveError("FillMandatory", MsgKt.getElementTranslation("PA_Ratio_ID"));
            return false;
        } else if (X_PA_Measure.MEASURETYPE_UserDefined.equals(getMeasureType())
                && (getCalculationClass() == null || getCalculationClass().length() == 0)) {
            log.saveError("FillMandatory", MsgKt.getElementTranslation("CalculationClass"));
            return false;
        } else if (X_PA_Measure.MEASURETYPE_Request.equals(getMeasureType())
                && getRequestTypeId() == 0) {
            log.saveError("FillMandatory", MsgKt.getElementTranslation("R_RequestType_ID"));
            return false;
        } else if (X_PA_Measure.MEASURETYPE_Project.equals(getMeasureType())
                && getProjectTypeId() == 0) {
            log.saveError("FillMandatory", MsgKt.getElementTranslation("C_ProjectType_ID"));
            return false;
        }
        return true;
    } //	beforeSave

    /**
     * After Save
     *
     * @param newRecord new
     * @param success   success
     * @return succes
     */
    protected boolean afterSave(boolean newRecord, boolean success) {
        //	Update Goals with Manual Measure
        if (success && X_PA_Measure.MEASURETYPE_Manual.equals(getMeasureType())) updateManualGoals();

        return success;
    } //	afterSave

    /**
     * Update/save Goals
     *
     * @return true if updated
     */
    public boolean updateGoals() {
        String mt = getMeasureType();
        try {
            if (X_PA_Measure.MEASURETYPE_Manual.equals(mt)) return updateManualGoals();
            else if (X_PA_Measure.MEASURETYPE_Achievements.equals(mt)) return updateAchievementGoals();
            else if (X_PA_Measure.MEASURETYPE_Calculated.equals(mt)) return updateCalculatedGoals();
            else if (X_PA_Measure.MEASURETYPE_Ratio.equals(mt)) return updateRatios();
            else if (X_PA_Measure.MEASURETYPE_Request.equals(mt)) return updateRequests();
            else if (X_PA_Measure.MEASURETYPE_Project.equals(mt)) return updateProjects();
            else if (X_PA_Measure.MEASURETYPE_UserDefined.equals(mt)) return updateUserDefined();
            //	Projects
        } catch (Exception e) {
            log.log(Level.SEVERE, "MeasureType=" + mt, e);
        }
        return false;
    } //	updateGoals

    /**
     * Update/save Manual Goals
     *
     * @return true if updated
     */
    private boolean updateManualGoals() {
        if (!X_PA_Measure.MEASURETYPE_Manual.equals(getMeasureType())) return false;
        MGoal[] goals = MGoal.getMeasureGoals(getMeasureId());
        for (MGoal goal : goals) {
            goal.setMeasureActual(getManualActual());
            goal.saveEx();
        }
        return true;
    } //	updateManualGoals

    /**
     * Update/save Goals with Achievement
     *
     * @return true if updated
     */
    private boolean updateAchievementGoals() {
        if (!X_PA_Measure.MEASURETYPE_Achievements.equals(getMeasureType())) return false;
        Timestamp today = new Timestamp(System.currentTimeMillis());
        MGoal[] goals = MGoal.getMeasureGoals(getMeasureId());
        for (MGoal goal : goals) {
            String MeasureScope = goal.getMeasureScope();
            String trunc = TimeUtil.TRUNC_DAY;
            if (MGoal.MEASUREDISPLAY_Year.equals(MeasureScope)) trunc = TimeUtil.TRUNC_YEAR;
            else if (MGoal.MEASUREDISPLAY_Quarter.equals(MeasureScope)) trunc = TimeUtil.TRUNC_QUARTER;
            else if (MGoal.MEASUREDISPLAY_Month.equals(MeasureScope)) trunc = TimeUtil.TRUNC_MONTH;
            else if (MGoal.MEASUREDISPLAY_Week.equals(MeasureScope)) trunc = TimeUtil.TRUNC_WEEK;
            Timestamp compare = TimeUtil.trunc(today, trunc);
            //
            MAchievement[] achievements = MAchievement.getOfMeasure(getMeasureId());
            BigDecimal ManualActual = Env.ZERO;
            for (MAchievement achievement : achievements) {
                if (achievement.isAchieved() && achievement.getDateDoc() != null) {
                    Timestamp ach = TimeUtil.trunc(achievement.getDateDoc(), trunc);
                    if (compare.equals(ach)) ManualActual = ManualActual.add(achievement.getManualActual());
                }
            }
            goal.setMeasureActual(ManualActual);
            goal.saveEx();
        }
        return true;
    } //	updateAchievementGoals

    /**
     * Update/save Goals with Calculation
     *
     * @return true if updated
     */
    private boolean updateCalculatedGoals() {
        if (!X_PA_Measure.MEASURETYPE_Calculated.equals(getMeasureType())) return false;
        MGoal[] goals = MGoal.getMeasureGoals(getMeasureId());
        for (MGoal goal : goals) {
            //	Find Role
            MRole role = null;
            if (goal.getRoleId() != 0) role = MRoleKt.getRole(goal.getRoleId());
            else if (goal.getUserId() != 0) {
                MUser user = MUserKt.getUser(goal.getUserId());
                MRole[] roles = user.getRoles(goal.getOrgId());
                if (roles.length > 0) role = roles[0];
            }
            if (role == null) role = MRoleKt.getDefaultRole(false); // 	could result in wrong data
            //
            MMeasureCalc mc = MMeasureCalc.get(getMeasureCalcId());
            if (mc == null || mc.getId() == 0 || mc.getId() != getMeasureCalcId()) {
                log.log(Level.SEVERE, "Not found PA_MeasureCalc_ID=" + getMeasureCalcId());
                return false;
            }
            String sql =
                    mc.getSqlPI(
                            goal.getRestrictions(false),
                            goal.getMeasureScope(),
                            getMeasureDataType(),
                            null,
                            role);
            BigDecimal ManualActual = getSQLValueBD(sql);
            //	SQL may return no rows or null
            if (ManualActual == null) {
                ManualActual = Env.ZERO;
                if (log.isLoggable(Level.FINE)) log.fine("No Value = " + sql);
            }
            goal.setMeasureActual(ManualActual);
            goal.saveEx();
        }
        return true;
    } //	updateCalculatedGoals

    /**
     * Update/save Goals with Ratios
     *
     * @return true if updated
     */
    private boolean updateRatios() {
        if (!X_PA_Measure.MEASURETYPE_Ratio.equals(getMeasureType())) return false;
        return false;
    } //	updateRatios

    /**
     * Update/save Goals with Requests
     *
     * @return true if updated
     */
    private boolean updateRequests() {
        if (!X_PA_Measure.MEASURETYPE_Request.equals(getMeasureType()) || getRequestTypeId() == 0)
            return false;
        MGoal[] goals = MGoal.getMeasureGoals(getMeasureId());
        for (MGoal goal : goals) {
            //	Find Role
            MRole role = null;
            if (goal.getRoleId() != 0) role = MRoleKt.getRole(goal.getRoleId());
            else if (goal.getUserId() != 0) {
                MUser user = MUserKt.getUser(goal.getUserId());
                MRole[] roles = user.getRoles(goal.getOrgId());
                if (roles.length > 0) role = roles[0];
            }
            if (role == null) role = MRoleKt.getDefaultRole(false); // 	could result in wrong data
            //
            MRequestType rt = MRequestType.get(getRequestTypeId());
            String sql =
                    rt.getSqlPI(
                            goal.getRestrictions(false),
                            goal.getMeasureScope(),
                            getMeasureDataType(),
                            null,
                            role);
            BigDecimal ManualActual = getSQLValueBD(sql);
            //	SQL may return no rows or null
            if (ManualActual == null) {
                ManualActual = Env.ZERO;
                if (log.isLoggable(Level.FINE)) log.fine("No Value = " + sql);
            }
            goal.setMeasureActual(ManualActual);
            goal.saveEx();
        }
        return true;
    } //	updateRequests

    /**
     * Update/save Goals with Projects
     *
     * @return true if updated
     */
    private boolean updateProjects() {
        if (!X_PA_Measure.MEASURETYPE_Project.equals(getMeasureType()) || getProjectTypeId() == 0)
            return false;
        MGoal[] goals = MGoal.getMeasureGoals(getMeasureId());
        for (MGoal goal : goals) {
            //	Find Role
            MRole role = null;
            if (goal.getRoleId() != 0) role = MRoleKt.getRole(goal.getRoleId());
            else if (goal.getUserId() != 0) {
                MUser user = MUserKt.getUser(goal.getUserId());
                MRole[] roles = user.getRoles(goal.getOrgId());
                if (roles.length > 0) role = roles[0];
            }
            if (role == null) role = MRoleKt.getDefaultRole(false); // 	could result in wrong data
            //
            MProjectType pt = MProjectType.get(getProjectTypeId());
            String sql =
                    pt.getSqlPI(
                            goal.getRestrictions(false),
                            goal.getMeasureScope(),
                            getMeasureDataType(),
                            null,
                            role);
            BigDecimal ManualActual = getSQLValueBD(sql);
            //	SQL may return no rows or null
            if (ManualActual == null) {
                ManualActual = Env.ZERO;
                if (log.isLoggable(Level.FINE)) log.fine("No Value = " + sql);
            }
            goal.setMeasureActual(ManualActual);
            goal.saveEx();
        }
        return true;
    } //	updateProjects

    /**
     * Update/save update User Defined
     *
     * @return true if updated
     */
    private boolean updateUserDefined() {
        MGoal[] goals = MGoal.getMeasureGoals(getMeasureId());
        for (MGoal goal : goals) {
            BigDecimal amt = Env.ZERO;
            PO po =
                    (PO) new MTable(getTableId()).getPO(getId());
            StringTokenizer st = new StringTokenizer(getCalculationClass(), ";,", false);
            while (st.hasMoreTokens()) //  for each class
            {
                String cmd = st.nextToken().trim();
                StringBuilder retValue = new StringBuilder();
                if (cmd.toLowerCase().startsWith(MRule.SCRIPT_PREFIX)) {

                    Rule rule = MRule.get(cmd.substring(MRule.SCRIPT_PREFIX.length()));
                    if (rule == null) {
                        retValue = new StringBuilder("Script ").append(cmd).append(" not found");
                        log.log(Level.SEVERE, retValue.toString());
                        break;
                    }
                    if (!(rule.getEventType().equals(MRule.EVENTTYPE_MeasureForPerformanceAnalysis)
                            && rule.getRuleType().equals(MRule.RULETYPE_JSR223ScriptingAPIs))) {
                        retValue =
                                new StringBuilder("Script ")
                                        .append(cmd)
                                        .append(" must be of type JSR 223 and event measure");
                        log.log(Level.SEVERE, retValue.toString());
                        break;
                    }
                    ScriptEngine engine = rule.getScriptEngine();
                    engine.put(MRule.ARGUMENTS_PREFIX + "PO", po);
                    try {
                        Object value = engine.eval(rule.getScript());
                        amt = (BigDecimal) value;
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "", e);
                        return false;
                    }
                } else {
                    MeasureInterface custom;
                    try {
                        Class<?> clazz = Class.forName(cmd);
                        custom = (MeasureInterface) clazz.newInstance();
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "No custom measure class " + cmd + " - " + e.toString(), e);
                        return false;
                    }

                    try {
                        amt = custom.getValue();
                    } catch (Exception e) {
                        log.log(Level.SEVERE, custom.toString(), e);
                        return false;
                    }
                }

                if (!Util.isEmpty(retValue.toString())) // 	interrupt on first error
                {
                    log.severe(retValue.toString());
                    return false;
                }
            }
            goal.setMeasureActual(amt);
            goal.saveEx();
        }
        return true;
    } //	updateUserDefinedGoals
} //	MMeasure
