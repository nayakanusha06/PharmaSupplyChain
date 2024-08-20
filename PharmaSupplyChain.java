import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Drug {
    public String name;
    public String manufacturer;
    public String serialNumber;

    public Drug(String name, String manufacturer, String serialNumber) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "Drug{name='" + name + "', manufacturer='" + manufacturer + "', serialNumber='" + serialNumber + "'}";
    }
}

class Transaction {
    public String sender;
    public String receiver;
    public Drug drug;
    public long timestamp;

    public Transaction(String sender, String receiver, Drug drug) {
        this.sender = sender;
        this.receiver = receiver;
        this.drug = drug;
        this.timestamp = new Date().getTime();
    }

    @Override
    public String toString() {
        return "Transaction{sender='" + sender + "', receiver='" + receiver + "', drug=" + drug + ", timestamp=" + timestamp + "}";
    }
}

class Block {
    public String previousHash;
    public String hash;
    public List<Transaction> transactions = new ArrayList<>();
    public long timestamp;
    public int nonce;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String dataToHash = previousHash + Long.toString(timestamp) + transactions.toString() + Integer.toString(nonce);
        return Integer.toHexString(dataToHash.hashCode());
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        System.out.println("Mining block..."); // Debugging
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined: " + hash); // Debugging
    }
}

class Blockchain {
    public List<Block> chain = new ArrayList<>();
    public int difficulty;

    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        chain.add(createGenesisBlock());
    }

    public Block createGenesisBlock() {
        return new Block("0");
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block block) {
        block.previousHash = getLatestBlock().hash;
        System.out.println("Adding block..."); // Debugging
        block.mineBlock(difficulty);
        chain.add(block);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }

            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                return false;
            }
        }
        return true;
    }
}

public class PharmaSupplyChain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Blockchain pharmaChain = new Blockchain(2); // Reduce difficulty for faster mining

        System.out.println("Enter drug 1 details:");
        System.out.print("Name: ");
        String name1 = scanner.nextLine();
        System.out.print("Manufacturer: ");
        String manufacturer1 = scanner.nextLine();
        System.out.print("Serial Number: ");
        String serialNumber1 = scanner.nextLine();
        Drug drug1 = new Drug(name1, manufacturer1, serialNumber1);

        Block block1 = new Block(pharmaChain.getLatestBlock().hash);
        block1.addTransaction(new Transaction("Manufacturer", "Distributor", drug1));
        pharmaChain.addBlock(block1);

        System.out.println("Enter drug 2 details:");
        System.out.print("Name: ");
        String name2 = scanner.nextLine();
        System.out.print("Manufacturer: ");
        String manufacturer2 = scanner.nextLine();
        System.out.print("Serial Number: ");
        String serialNumber2 = scanner.nextLine();
        Drug drug2 = new Drug(name2, manufacturer2, serialNumber2);

        Block block2 = new Block(pharmaChain.getLatestBlock().hash);
        block2.addTransaction(new Transaction("Distributor", "Pharmacy", drug2));
        pharmaChain.addBlock(block2);

        System.out.println("Blockchain is valid: " + pharmaChain.isChainValid());

        for (Block block : pharmaChain.chain) {
            System.out.println("\nBlock: " + block.hash);
            for (Transaction transaction : block.transactions) {
                System.out.println(transaction);
            }
        }
        scanner.close();
    }
}
