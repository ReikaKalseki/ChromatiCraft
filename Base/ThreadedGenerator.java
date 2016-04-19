package Reika.ChromatiCraft.Base;


public abstract class ThreadedGenerator {

	public abstract void run() throws Throwable;

	public abstract String getStateMessage();

}
