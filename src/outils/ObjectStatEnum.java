package outils;

public enum ObjectStatEnum {
	No_LOCK,
	READ_LOCK_CACHED,
	WRITE_LOCK_CACHED,
	READ_LOCK_TAKEN,
	WRITE_LOCK_TAKEN,
	READ_LOCK_TAKEN_WRITE_LOCK_CACHED;
}
