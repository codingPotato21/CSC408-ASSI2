package part2_2;

import java.util.Date;

public class Block {
	
	public String hash;
	public String previousHash; 
	private String data; //our data will be a simple message.
	private long timeStamp; //time as a number of milliseconds since 1/1/1970.
	private int nonce=0;
	private int difficulty;
	
	//Block Constructor.  
	public Block(String data, String previousHash, int difficulty) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.difficulty = difficulty;
		
		this.hash = calculateHash(); //Making sure we do this after we set the other values.
	}
	
	//Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedhash = StringUtil.applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				data 
				);
		return calculatedhash;
	}
	
	//Increases nonce value until hash target is reached.
	public int mineBlock() {
		String target = StringUtil.getDificultyString(difficulty); //Create a string with difficulty * "0" 
		if (!hash.substring( 0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
			return -1;
		}
		System.out.println("Block Mined!!! : " + hash);
		System.out.println("Previouse hash: " + previousHash);
		System.out.println("Block Nonce: " + nonce);
		return 1;
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
}
