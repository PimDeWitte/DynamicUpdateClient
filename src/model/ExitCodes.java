package model;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         12/7/14
 *         model
 *         ${FILE_NAME}
 */
public enum ExitCodes {

    NEW_STAGE(-1),
    EXECUTED_SUCCESSFULLY(0),
    FAIL_AND_RETRY(1);

    private final int code;

    private ExitCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
