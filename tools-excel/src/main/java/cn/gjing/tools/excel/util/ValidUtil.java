package cn.gjing.tools.excel.util;

import cn.gjing.tools.excel.write.valid.Rank;
import org.apache.poi.ss.usermodel.DataValidation;

/**
 * @author Gjing
 **/
public class ValidUtil {

    public static void setErrorBox(DataValidation dataValidation, boolean error, Rank rank, String errTitle, String errMsg,
                                   boolean prompt, String pTitle, String pMsg) {
        dataValidation.setShowErrorBox(error);
        dataValidation.setErrorStyle(rank.getRank());
        dataValidation.createErrorBox(errTitle, errMsg);
        dataValidation.setShowPromptBox(prompt);
        dataValidation.createPromptBox(pTitle, pMsg);
    }
}
