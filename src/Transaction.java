import java.io.Serializable;
import java.security.PublicKey;
import java.security.Signature;

public final class Transaction implements Serializable {

    public final PublicKey sender;
    public final String receiver;
    public final double amount;
    private byte[] signature;

    public Transaction(PublicKey sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public void signTransaction(Wallet wallet) {
        String data = getData();
        this.signature = wallet.sign(data.getBytes());
    }

    private String getData() {
        return sender.toString() + receiver + amount;
    }

    public boolean isValid() {
        try {
            if (signature == null) return false;
            Signature verify = Signature.getInstance("SHA256withRSA");
            verify.initVerify(sender);
            verify.update(getData().getBytes());
            return verify.verify(signature);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "TX[" + amount + "] → " + receiver;
    }
}
