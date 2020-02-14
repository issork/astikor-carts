package de.mennomax.astikorcarts.util;

public final class Quat4f {
	public static Quat4f UNIT = new Quat4f();

	private static final float EPS = 1e-6F;

	public final float x;

	public final float y;

	public final float z;

	public final float w;

	public Quat4f() {
		this(0.0F, 0.0F, 0.0F, 1.0F);
	}

	public Quat4f(final Quat4f q) {
		this(q.x, q.y, q.z, q.w);
	}

	public Quat4f(final double x, final double y, final double z, final double w) {
		this((float) x, (float) y, (float) z, (float) w);
	}

	public Quat4f(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float dot(final Quat4f q) {
		return this.w * q.w + this.x * q.x + this.y * q.y + this.z * q.z;
	}

	public Quat4f negate() {
		return new Quat4f(-this.x, -this.y, -this.z, -this.w);
	}

	public float norm() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	public Quat4f normalize() {
		final double n = 1.0D / Math.sqrt(this.norm());
		return new Quat4f(this.x * n, this.y * n, this.z * n, this.w * n);
	}

	public Quat4f interpolate(Quat4f q2, final float t) {
		if (this.x == q2.x && this.y == q2.y && this.z == q2.z && this.w == q2.w) {
			return this;
		}
		float dot = this.dot(q2);
		if (dot < 0.0D) {
			q2 = q2.negate();
			dot = -dot;
		}
		final double s0;
		final double s1;
		if (1 - dot > EPS) {
			final double theta = Math.acos(dot);
			final double invSinTheta = Math.sin(theta);
			s0 = Math.sin((1 - t) * theta) / invSinTheta;
			s1 = Math.sin(t * theta) / invSinTheta;
		} else {
			s0 = 1 - t;
			s1 = t;
		}
		return new Quat4f(
			s0 * this.x + s1 * q2.x,
			s0 * this.y + s1 * q2.y,
			s0 * this.z + s1 * q2.z,
			s0 * this.w + s1 * q2.w
		);
	}
	
	public Quat4f mul(final Quat4f other) {
		return new Quat4f(
			this.x * other.w + this.y * other.z - this.z * other.y + this.w * other.x,
			-this.x * other.z + this.y * other.w + this.z * other.x + this.w * other.y,
			this.x * other.y - this.y * other.x + this.z * other.w + this.w * other.z,
			-this.x * other.x - this.y * other.y - this.z * other.z + this.w * other.w
		);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Quat4f) {
			final Quat4f other = (Quat4f) obj;
			return Float.compare(this.x, other.x) == 0 &&
				Float.compare(this.y, other.y) == 0 &&
				Float.compare(this.z, other.z) == 0 &&
				Float.compare(this.w, other.w) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash = 31 * hash + Long.hashCode(Float.floatToIntBits(this.x));
		hash = 31 * hash + Long.hashCode(Float.floatToIntBits(this.y));
		hash = 31 * hash + Long.hashCode(Float.floatToIntBits(this.z));
		hash = 31 * hash + Long.hashCode(Float.floatToIntBits(this.w));
		return hash;
	}

	@Override
	public String toString() {
		return "Quat4f{" +
			"x=" + this.x +
			", y=" + this.y +
			", z=" + this.z +
			", w=" + this.w +
			'}';
	}

	public static Quat4f fromAxisAngle(final float x, final float y, final float z, final float angle) {
		final double sin = Math.sin(0.5D * angle);
		return new Quat4f(x * sin, y * sin, z * sin, Math.cos(0.5D * angle));
	}
}
