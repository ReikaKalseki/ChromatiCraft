package Reika.ChromatiCraft.Auxiliary.Interfaces;


public interface OperationInterval {

	public float getOperationFraction();

	public OperationState getState();

	public static enum OperationState {

		INVALID(),
		PENDING(),
		RUNNING();

	}

}
