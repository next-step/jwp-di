package next.service;

class DataAccessException extends IllegalStateException {

    DataAccessException(String message) {
        super(message);
    }
}