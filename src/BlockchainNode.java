public class BlockchainNode {

    private String name;
    private Blockchain blockchain;

    public BlockchainNode(String name) {
        this.name = name;
        this.blockchain = new Blockchain();
    }

    public String getName() {
        return name;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }
}
