package by.laba1.demo.api.error;

public class MessageException {
    public static final String ERROR = "error";
    public static final String ENTITY_WITH_ID_NOT_FOUND = "Entity with ID %d not found";
    public static final String ENTITY_WITH_CRITERIA_NOT_FOUND = "No entity found matching the criteria";
    public static final String SPECIALIZATION_REQUIRED = "Specialization is required";
    public static final String PHONE_NUMBER_UNIQUE = "Patient with this phone number already exists: ";
    public static final String INVALID_JSON = "Invalid JSON format";
    public static final String CONSTRAINT_VIOLATION = "Validation error: %s";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";
}
