import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    public static String getMerkleRoot(List<Transaction> transactions) {

        if (transactions == null || transactions.isEmpty())
            return "";

        List<String> hashes = new ArrayList<>();

        for (Transaction tx : transactions) {
            hashes.add(HashUtil.applySHA256(tx.toString()));
        }

        while (hashes.size() > 1) {
            List<String> next = new ArrayList<>();

            for (int i = 0; i < hashes.size(); i += 2) {
                String left = hashes.get(i);
                String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left;
                next.add(HashUtil.applySHA256(left + right));
            }

            hashes = next;
        }
        return hashes.get(0);
    }
}
