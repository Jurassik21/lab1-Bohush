package ua.lpnu.kzp;

/** Повідомляє про помилку зберігання, CSV або рефлексії. */
public class DataStorageException extends Exception {
    public DataStorageException(String message) {
        super(message);
    }

    public DataStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
