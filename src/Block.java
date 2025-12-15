import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable {

    public final int index;
    public final long timestamp;
    public final List<Transaction> transactions;
    public final String previousHash;

    public String hash;
    public String merkleRoot;
    public int nonce;

    public Block(int index, List<Transaction> txs, String previousHash) {
        this.index = index;
        this.transactions = new ArrayList<>(txs);
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.merkleRoot = MerkleTree.getMerkleRoot(this.transactions);
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return HashUtil.applySHA256(
                index + previousHash + timestamp + merkleRoot + nonce
        );
    }

    public void mineBlock(int difficulty) {
        String target = "0".repeat(difficulty);
        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash();
        }
    }
}
