import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BankTest {
    @Mock
    private BankInterface bank;
    private ATM atm;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        atm = new ATM(bank);
    }

    @Test
    @DisplayName("Testar insättning av kort")
    void testInsertCard() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);
        when(bank.isCardLocked("123")).thenReturn(false);

        assertTrue(atm.insertCard("123"));
        verify(bank).getUserById("123");
    }

    @Test
    @DisplayName("Testa inmatning av korrekt PIN")
    void testEnterCorrectPin() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        assertTrue(atm.enterPin("1234"));
    }

    @Test
    @DisplayName("Testa låsning efter tre felaktiga PIN-försök")
    void testPinLockAfterThreeFailures() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("0000");
        atm.enterPin("0000");
        assertFalse(atm.enterPin("0000"));
        assertTrue(user.isLocked());
    }

    @Test
    @DisplayName("Testa kort som redan är låst")
    void testLockedCard() {
        User user = new User("123", "1234", 1000.0);
        user.lockCard();

        when(bank.getUserById("123")).thenReturn(user);
        atm.insertCard("123");
        atm.enterPin("1234");
        verify(bank).isCardLocked("123");
        assertTrue(user.isLocked());
    }

    @Test
    @DisplayName("Testa kontrollera saldo")
    void testCheckBalance() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("1234");

        assertEquals(1000.0, atm.checkBalance());
    }

    @Test
    @DisplayName("Testa insättning")
    void testDeposit() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("1234");

        atm.deposit(500.0);
        assertEquals(1500.0, user.getBalance());
    }

    @Test
    @DisplayName("Testa insättning med noll belopp")
    void testDepositZeroAmount() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("1234");

        atm.deposit(0);
        assertEquals(1000.0, user.getBalance());
    }

    @Test
    @DisplayName("Testa uttag med tillräckligt saldo")
    void testWithdrawWithSufficientBalance() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("1234");

        assertTrue(atm.withdraw(500.0));
        assertEquals(500.0, user.getBalance());
    }

    @Test
    @DisplayName("Testa uttag med otillräckligt saldo")
    void testWithdrawWithInsufficientBalance() {
        User user = new User("123", "1234", 100.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("1234");

        assertFalse(atm.withdraw(500.0));
        assertEquals(100.0, user.getBalance());
    }

    @Test
    @DisplayName("Testa uttag av noll belopp")
    void testWithdrawZeroAmount() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("1234");

        assertFalse(atm.withdraw(0));
        assertEquals(1000.0, user.getBalance());
    }

    @Test
    @DisplayName("Testa avslutning av session")
    void testEndSession() {
        User user = new User("123", "1234", 1000.0);
        when(bank.getUserById("123")).thenReturn(user);

        atm.insertCard("123");
        atm.enterPin("1234");
        atm.endSession();

        assertFalse(atm.enterPin("1234"));
    }

    @Test
    @DisplayName("Testa insättning utan att vara inloggad")
    void testDepositWithoutLogin() {
        User user = new User("123", "1234", 1000.0);
        atm.deposit(500);
        assertEquals(1000.0, user.getBalance());
    }

    @Test
    @DisplayName("Testa uttag utan att vara inloggad")
    void testWithdrawWithoutLogin() {
        User user = new User("123", "1234", 1000.0);
        assertFalse(atm.withdraw(100));
        assertEquals(1000.0, user.getBalance());
    }

    @Test
    @DisplayName("Testa att kort inte kan sättas in om det är låst")
    void testInsertLockedCard() {
        User user = new User("456", "5678", 2000.0);
        user.lockCard();

        when(bank.getUserById("456")).thenReturn(user);
        when(bank.isCardLocked("456")).thenReturn(true);

        assertFalse(atm.insertCard("456"));
    }

    @Test
    @DisplayName("Testa bankens namn")
    void testGetBankName() {
        when(bank.getBankName()).thenReturn("Nordea");
        assertEquals("Nordea", atm.getBankName());
    }
}
