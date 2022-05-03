package lab3;

import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.InvalidKeyException;

/**
 * Ledger defines an ledger in the ledger model of bitcoins it extends
 * UserAmount
 */

public class Ledger extends UserAmount {

	public boolean checkUserAmountDeductable(UserAmount userAmountCheck) {

		for (PublicKey publicKey : userAmountCheck.getPublicKeys()) {
			if (getBalance(publicKey) < userAmountCheck.getBalance(publicKey))
				return false;
		}
		;
		return true;
	};

	public boolean checkInputListDeductable(InputList inputList) {
		return checkUserAmountDeductable(inputList.toLedger());
	};

	public void subtractInputList(InputList inputList) {
		for (Input entry : inputList.toList()) {
			subtractFromBalance(entry.getSender(), entry.getAmount());
		}
	}

	public void addOutputList(OutputList outputList) {
		for (Output entry : outputList.toList()) {
			addBalance(entry.getRecipient(), entry.getAmount());
		}
	}

	public boolean checkTransactionValid(Transaction tx) {
		boolean flag = true;

		if (tx.checkSignaturesValid() & tx.checkTransactionAmountsValid()) {
			flag = checkUserAmountDeductable(tx.toInputs().toLedger());
		} else {
			flag = false;
		}
		return flag;
	};

	public void processTransaction(Transaction tx) {
		subtractInputList(tx.toInputs());
		addOutputList(tx.toOutputs());
	};

	/**
	 * Prints the current state of the ledger.
	 */

	public void print(PublicKeyMap pubKeyMap) {
		for (PublicKey publicKey : publicKeyList) {
			Integer value = getBalance(publicKey);
			System.out.println("The balance for " + pubKeyMap.getUser(publicKey) + " is " + value);
		}

	}

	/**
	 * Testcase
	 */

	public static void test() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {

		Wallet exampleWallet = SampleWallet.generate(new String[] { "Alice" });
		byte[] exampleMessage = KeyUtils.integer2ByteArray(1);
		byte[] exampleSignature = exampleWallet.signMessage(exampleMessage, "Alice");

		// Test 1

		System.out.println("Test 1\n");
		Wallet aliceWallet = SampleWallet.generate(new String[] { "A1", "A2" });
		Wallet bobWallet = SampleWallet.generate(new String[] { "B1", "B2" });
		Wallet carolWallet = SampleWallet.generate(new String[] { "C1", "C2", "C3" });
		Wallet davidWallet = SampleWallet.generate(new String[] { "D1" });

		// Test 2
		System.out.println("Test 2\n");
		PublicKeyMap keyMap = new PublicKeyMap();
		keyMap.addPublicKeyMap(aliceWallet.toPublicKeyMap());
		keyMap.addPublicKeyMap(bobWallet.toPublicKeyMap());
		keyMap.addPublicKeyMap(carolWallet.toPublicKeyMap());
		keyMap.addPublicKeyMap(davidWallet.toPublicKeyMap());
		System.out.println(keyMap.getUsers());

		// Test 3
		System.out.println("Test 2\n");

		UserAmount amount = new UserAmount();

		for (String user : keyMap.getUsers()) {
			amount.addAccount(keyMap.getPublicKey(user),0);
		}
		System.out.println("A1: " + amount.getBalance(aliceWallet.getPublicKey("A1")));
	}

	/**
	 * main function running test cases
	 */

	public static void main(String[] args) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		Ledger.test();
	}
}
