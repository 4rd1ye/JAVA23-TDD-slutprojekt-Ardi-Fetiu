public interface BankInterface {
    String getBankName();
    User getUserById(String userId);
    boolean isCardLocked(String userId);
}
