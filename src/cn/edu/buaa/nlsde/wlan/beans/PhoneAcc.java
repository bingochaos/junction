package cn.edu.buaa.nlsde.wlan.beans;

public class PhoneAcc {
	private float x;
	private float y;
	private float z;

	public PhoneAcc() {

	}

	public PhoneAcc(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "phoneAcc [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if ( !(other instanceof PhoneAcc))
			return false;
		final PhoneAcc acc = (PhoneAcc)other;
		if (!(getX() == acc.getX()))
			return false;
		if (!(getY() == acc.getY()))
			return false;
		if (!(getZ() == acc.getZ()))
			return false;
		return true;
	}
}
