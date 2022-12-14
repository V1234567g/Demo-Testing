package software.amazon.awssdk.services.rules;

public enum ValidationErrorType {
    INCONSISTENT_PARAMETER_TYPE,
    UNSUPPORTED_PARAMETER_TYPE,
    PARAMETER_MISMATCH,
    PARAMETER_TYPE_MISMATCH,
    SERVICE_ID_MISMATCH,
    REQUIRED_PARAMETER_MISSING,
    PARAMETER_IS_NOT_USED,
    PARAMETER_IS_NOT_DEFINED,

    INVALID_URI,
}
