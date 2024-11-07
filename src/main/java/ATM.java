public class ATM {
    private BankInterface bank;
    private User currentUser;

    public ATM(BankInterface bank) {
        this.bank = bank;
    }

    public boolean insertCard(String userId) {
        if (bank.isCardLocked(userId)) {
            System.out.println("Kortet är låst.");
            return false;
        }
        currentUser = bank.getUserById(userId);
        return currentUser != null;
    }

    public boolean enterPin(String pin) {
        if (currentUser == null) return false;

        if (currentUser.getPin().equals(pin)) {
            currentUser.resetFailedAttempts();
            return true;
        } else {
            currentUser.incrementFailedAttempts();
            if (currentUser.getFailedAttempts() >= 3) {
                currentUser.lockCard();
                System.out.println("Kortet har låsts.");
            } else {
                System.out.println("Fel PIN. Försök kvar: " + (3 - currentUser.getFailedAttempts()));
            }
            return false;
        }
    }

    public double checkBalance() {
        return currentUser != null ? currentUser.getBalance() : 0;
    }

    public void deposit(double amount) {
        if (currentUser != null && amount > 0) {
            currentUser.deposit(amount);
            System.out.println("Insättning lyckades: " + amount + " kr.");
        } else {
            System.out.println("Felaktigt belopp eller ej inloggad.");
        }
    }

    public boolean withdraw(double amount) {
        if (currentUser != null && amount > 0 && currentUser.getBalance() >= amount) {
            currentUser.withdraw(amount);
            System.out.println("Uttag lyckades: " + amount + " kr.");
            return true;
        }
        System.out.println("Otillräckligt saldo eller felaktigt belopp.");
        return false;
    }

    public void endSession() {
        currentUser = null;
        System.out.println("Session avslutad.");
    }

    public String getBankName() {
        return bank.getBankName();
    }
}
