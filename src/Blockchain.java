import java.util.*;

public class Blockchain {

    private final List<Block> chain = new ArrayList<>();
    private final List<Transaction> mempool = new ArrayList<>();
    private final List<Integer> difficultyHistory = new ArrayList<>();
    private static final long TARGET_BLOCK_TIME = 5000; // 5 seconds
    private static final int ADJUST_INTERVAL = 2;        // every 2 blocks


    private int difficulty = 4;

    public Blockchain() {
        chain.add(new Block(0, new ArrayList<>(), "0"));
    }

    public void addTransaction(Transaction tx) {
        mempool.add(tx);
    }

    public void addBlock(Block block) {

        for (Transaction tx : block.transactions) {
            if (!tx.isValid()) {
                System.out.println("❌ Invalid transaction. Block rejected.");
                return;
            }
        }

        block.mineBlock(difficulty);
        chain.add(block);
        difficultyHistory.add(difficulty);
        mempool.clear();

        adjustDifficulty();
    }

    public boolean isChainValid() {

        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);

            if (!current.merkleRoot.equals(
                    MerkleTree.getMerkleRoot(current.transactions)))
                return false;

            if (!current.hash.equals(current.calculateHash()))
                return false;

            if (!current.previousHash.equals(previous.hash))
                return false;
        }
        return true;
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public List<Block> getChain() {
        return chain;
    }

    public List<Transaction> getMempool() {
        return mempool;
    }

    public List<Integer> getDifficultyHistory() {
        return difficultyHistory;
    }

    public void tamperBlock(int index) {
        if (index > 0 && index < chain.size()) {
            Block block = chain.get(index);
            if (!block.transactions.isEmpty()) {
                try {
                    var f = Transaction.class.getDeclaredField("amount");
                    f.setAccessible(true);
                    f.set(block.transactions.get(0), 9999.0);
                } catch (Exception ignored) {}
            }
        }
    }
    private void adjustDifficulty() {

        if (chain.size() % ADJUST_INTERVAL != 0 || chain.size() < ADJUST_INTERVAL + 1)
            return;

        Block latest = chain.get(chain.size() - 1);
        Block prev = chain.get(chain.size() - 1 - ADJUST_INTERVAL);

        long actualTime = latest.timestamp - prev.timestamp;

        if (actualTime < TARGET_BLOCK_TIME) {
            difficulty++;
        } else if (difficulty > 1) {
            difficulty--;
        }
    }

}
