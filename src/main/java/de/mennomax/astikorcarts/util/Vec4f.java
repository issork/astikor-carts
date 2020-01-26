package de.mennomax.astikorcarts.util;

public final class Vec4f {
	private float x;

	private float y;

	private float z;

	private float w;

	public Vec4f(final double x, final double y, final double z, final double w) {
		this((float) x, (float) y, (float) z, (float) w);
	}

	public Vec4f(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float x() {
		return this.x;
	}

	public float y() {
		return this.y;
	}

	public float z() {
		return this.z;
	}

	public float w() {
		return this.w;
	}

	public void set(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vec4f transform(final Mat4f m) {
		final float x = this.x;
		final float y = this.y;
		final float z = this.z;
		final float w = this.w;
		this.x = m.m00 * x + m.m01 * y + m.m02 * z + m.m03 * w;
		this.y = m.m10 * x + m.m11 * y + m.m12 * z + m.m13 * w;
		this.z = m.m20 * x + m.m21 * y + m.m22 * z + m.m23 * w;
		this.w = m.m30 * x + m.m31 * y + m.m32 * z + m.m33 * w;
		return this;
	}

	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ')';
	}
}
