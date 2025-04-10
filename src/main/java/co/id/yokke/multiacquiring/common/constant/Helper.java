package co.id.yokke.multiacquiring.common.constant;

/**
 * Helper
 */
public class Helper {

    public final static String RED = "#d50000";
    public final static String PINK = "#C51162";
    public final static String PURPLE = "#AA00FF";
    public final static String DEEP_BLUE = "#6200EA";
    public final static String INDIGO = "#304FFE";
    public final static String BLUE = "#2962FF";
    public final static String LIGHT_BLUE = "#0091EA";
    public final static String CYAN = "#00B8D4";
    public final static String TEAL = "#00BFA5";
    public final static String GREEN = "#00C853";
    public final static String LIGHT_GREEN = "#64DD17";
    public final static String LIME = "#AEEA00";
    public final static String YELLOW = "#FFD600";
    public final static String AMBER = "#FFAB00";
    public final static String ORANGE = "#FF6D00";
    public final static String DEEP_ORANGE = "#DD2C00";
    public final static String BROWN = "#5D4037";
    public final static String GREY = "#616161";
    public final static String BLUE_GREY = "#455A64";

    public static String querySaleType(String saleType){
        switch (saleType){
            case "PURCHASE":
                return " AUTH_SALE_KND_CD='01' AND  MSG_TP_ID='0200' and (ARCV_PROS_CD_VAL = '000000' OR ARCV_PROS_CD_VAL = '001000' OR ARCV_PROS_CD_VAL = '491000') AND";
            case "INSTALLMENT":
                return " AUTH_SALE_KND_CD='02' AND  MSG_TP_ID='0200' and ARCV_PROS_CD_VAL='000000' AND";
            case "PREPAID_SALE":
                return " AUTH_SALE_KND_CD='11' AND  MSG_TP_ID='0220' and ARCV_PROS_CD_VAL='280000' AND";
            case "PREPAID_TOPUP":
                return " AUTH_SALE_KND_CD='12' AND  MSG_TP_ID='0200' and ARCV_PROS_CD_VAL='420000' AND";
            case "PREPAID_PENDING_TOPUP":
                return " AUTH_SALE_KND_CD='15' AND  MSG_TP_ID='0200' and ARCV_PROS_CD_VAL='490000' AND";
            default:
                return "";
        }
    }

    public static String queryChanelType(String chanelType){
        switch (chanelType){
            case "ecommerce":
                return " AUTH_REQ_CHNL_CD = '02' AND";
            case "edc":
                return " (AUTH_REQ_CHNL_CD = '01' OR AUTH_REQ_CHNL_CD = '08') AND";
            default:
                return "";
        }
    }

    public static String getSaleType(String AUTH_SALE_KND_CD, String MSG_TP_ID,
                                     String ARCV_PROS_CD_VAL, String AUTH_CNCL_RSON_CD){
        if(AUTH_SALE_KND_CD.equals("11"))
            return  "PREPAID SALE";
        else if(AUTH_SALE_KND_CD.equals("13"))
            return  "UPDATE BALANCE";
        else if(AUTH_SALE_KND_CD.equals("17"))
            return  "INFO DEPOSIT";
        else if(MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals( "000000") && AUTH_CNCL_RSON_CD.equals("void") )
            return  "VOID";
    else if(AUTH_SALE_KND_CD.equals("01") &&
            MSG_TP_ID.equals("0200") &&
            (ARCV_PROS_CD_VAL.equals( "000000") || ARCV_PROS_CD_VAL.equals("001000") || ARCV_PROS_CD_VAL.equals("491000")))
        return "PURCHASE";
    else if(AUTH_SALE_KND_CD.equals("01") &&
                MSG_TP_ID.equals("0400") &&
                (ARCV_PROS_CD_VAL.equals( "000000") || ARCV_PROS_CD_VAL.equals("001000") || ARCV_PROS_CD_VAL.equals("491000")))
        return "PURCHASE REVERSAL";
    else if(AUTH_SALE_KND_CD.equals("02") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("000000"))
        return "INSTALLMENT";
    else if(AUTH_SALE_KND_CD.equals("02") &&  MSG_TP_ID.equals("0400") && ARCV_PROS_CD_VAL.equals("000000"))
        return "INSTALLMENT REVERSAL";

    else if(AUTH_SALE_KND_CD.equals("03") &&  MSG_TP_ID.equals("0100") && ARCV_PROS_CD_VAL.equals("000000"))
        return "PREAUTH";

    else if(AUTH_SALE_KND_CD.equals("04") &&  MSG_TP_ID.equals("0220") && (ARCV_PROS_CD_VAL.equals("000000") || ARCV_PROS_CD_VAL.equals("003000") ))
        return "COMPLETION";

    else if(AUTH_SALE_KND_CD.equals("05") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("350000" ))
        return "WITHDRAWL";

    else if(AUTH_SALE_KND_CD.equals("06") &&  MSG_TP_ID.equals("0220") && ARCV_PROS_CD_VAL.equals("020000" ))
        return "ADJUST";

    else if(AUTH_SALE_KND_CD.equals("11") &&  MSG_TP_ID.equals("0220") && ARCV_PROS_CD_VAL.equals("280000" ))
        return "PREPAID_SALE";

    else if(AUTH_SALE_KND_CD.equals("12") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("420000" ))
        return "PREPAID_TOPUP";

    else if(AUTH_SALE_KND_CD.equals("13") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("440000" ))
        return "PREPAID_UPDATE_BAL";

    else if(AUTH_SALE_KND_CD.equals("13") &&  MSG_TP_ID.equals("0400") && ARCV_PROS_CD_VAL.equals("440000" ))
        return "PREPAID_UPDATE_BAL REVERSAL";

    else if(AUTH_SALE_KND_CD.equals("15") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("490000" ))
        return "PREPAID PENDING TOPUP";

    else if(AUTH_SALE_KND_CD.equals("21") && (MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("320000") || (MSG_TP_ID.equals("0100") && ARCV_PROS_CD_VAL.equals("551020"))))
        return "BILL_PAYMENT";

    else if(AUTH_SALE_KND_CD.equals("22") &&  MSG_TP_ID.equals("0200") && (ARCV_PROS_CD_VAL.equals("471000") || ARCV_PROS_CD_VAL.equals("481000") || ARCV_PROS_CD_VAL.equals("482000")))
        return "MONEY_TRANSFER";

    else if(AUTH_SALE_KND_CD.equals("23") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("471040" ))
        return "POST_DATE";

    else if(AUTH_SALE_KND_CD.equals("31") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("000000" ))
        return "LYLTY_REDEMP";

    else if(AUTH_SALE_KND_CD.equals("32") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("000000" ))
        return "LYLTY_REDEMP_INSTLL";

    else if(AUTH_SALE_KND_CD.equals("61") &&  MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("203000" ))
        return "REFUND";

    else if(AUTH_SALE_KND_CD.equals("61") &&  MSG_TP_ID.equals("0400") && ARCV_PROS_CD_VAL.equals("000000" ))
        return "REFUND REVERSAL";

    else if(AUTH_SALE_KND_CD.equals("12") &&  MSG_TP_ID.equals("0400") && ARCV_PROS_CD_VAL.equals("420000" ))
        return "PREPAID_TOPUP REVERSAL";
    else if(AUTH_SALE_KND_CD.equals("31") && MSG_TP_ID.equals("0400") && ARCV_PROS_CD_VAL.equals("000000" ))
        return "LYLTY_REVERSAL";

    else if(MSG_TP_ID.equals("0200") && ARCV_PROS_CD_VAL.equals("020000"))
        return "VOID";
    else if(MSG_TP_ID.equals("0400") && ARCV_PROS_CD_VAL.equals("020000"))
        return "VOID REVERSAL";
    else
        return "OTHER";
    }

    public static String trxStatus(String code){
        switch (code){
            case "00":
                return "Approve";
            case "90":
                return "Timeout";
            default:
                return "Decline";
        }
    }
}