package de.mennomax.astikorcarts.util;

import net.minecraft.util.Mth;

public final class Mat4f {
	public float m00, m01, m02, m03,
				 m10, m11, m12, m13,
				 m20, m21, m22, m23,
				 m30, m31, m32, m33;

	public Mat4f() {}

	public Mat4f(final Mat4f matrix) {
		this.m00 = matrix.m00;
		this.m01 = matrix.m01;
		this.m02 = matrix.m02;
		this.m03 = matrix.m03;
		this.m10 = matrix.m10;
		this.m11 = matrix.m11;
		this.m12 = matrix.m12;
		this.m13 = matrix.m13;
		this.m20 = matrix.m20;
		this.m21 = matrix.m21;
		this.m22 = matrix.m22;
		this.m23 = matrix.m23;
		this.m30 = matrix.m30;
		this.m31 = matrix.m31;
		this.m32 = matrix.m32;
		this.m33 = matrix.m33;
	}

	public Mat4f makeIdentity() {
		this.m00 = this.m11 = this.m22 = this.m33 = 1.0F;
		this.m01 = this.m02 = this.m03 = this.m10 = this.m12 = this.m13 = this.m20 = this.m21 = this.m23 = this.m30 = this.m31 = this.m32 = 0.0F;
		return this;
	}

	public Mat4f makeTranslation(final float x, final float y, final float z) {
		this.makeIdentity();
		this.m03 = x;
		this.m13 = y;
		this.m23 = z;
		return this;
	}

	public Mat4f makeScale(final float x, final float y, final float z) {
		this.makeIdentity();
		this.m00 = x;
		this.m11 = y;
		this.m22 = z;
		this.m33 = 1.0F;
		return this;
	}

	public Mat4f makeRotation(final float angle, final float x, final float y, final float z) {
		this.makeIdentity();
		final float c = Mth.cos(angle);
		final float s = Mth.sin(angle);
		final float t = 1.0F - c;
		this.m00 = c + x * x * t;
		this.m11 = c + y * y * t;
		this.m22 = c + z * z * t;
		float a = x * y * t;
		float b = z * s;
		this.m10 = a + b;
		this.m01 = a - b;
		a = x * z * t;
		b = y * s;
		this.m20 = a - b;
		this.m02 = a + b;
		a = y * z * t;
		b = x * s;
		this.m21 = a + b;
		this.m12 = a - b;
		return this;
	}

	public void makeQuaternion(final Quat4f quat) {
		this.makeQuaternion(quat.x, quat.y, quat.z, quat.w);
	}

	public void makeQuaternion(final float x, final float y, final float z, final float w) {
		this.makeIdentity();
		this.m00 = 1.0F - 2.0F * y * y - 2.0F * z * z;
		this.m10 = 2.0F * (x * y + w * z);
		this.m20 = 2.0F * (x * z - w * y);
		this.m01 = 2.0F * (x * y - w * z);
		this.m11 = 1.0F - 2.0F * x * x - 2.0F * z * z;
		this.m21 = 2.0F * (y * z + w * x);
		this.m02 = 2.0F * (x * z + w * y);
		this.m12 = 2.0F * (y * z - w * x);
		this.m22 = 1.0F - 2.0F * x * x - 2.0F * y * y;
	}

	public void makePerspective(final float fovy, final float aspect, final float zNear, final float zFar) {
		this.makeIdentity();
		final float radians = (float) Math.toRadians(fovy / 2.0F);
		final float deltaZ = zFar - zNear;
		final float sine = Mth.sin(radians);
		if (deltaZ == 0.0F || sine == 0.0F || aspect == 0.0F) {
			return;
		}
		final float cotangent = Mth.cos(radians) / sine;
		this.m00 = cotangent / aspect;
		this.m11 = cotangent;
		this.m22 = -(zFar + zNear) / deltaZ;
		this.m32 = -1.0F;
		this.m23 = -2.0F * zNear * zFar / deltaZ;
	}

	public void makeOrtho(final float left, final float right, final float bottom, final float top, final float zNear, final float zFar) {
		this.makeIdentity();
		this.m00 = 2.0F / (right - left);
		this.m11 = 2.0F / (top - bottom);
		this.m22 = -2.0F / (zFar - zNear);
		this.m03 = -(right + left) / (right - left);
		this.m13 = -(top + bottom) / (top - bottom);
		this.m23 = -(zFar - zNear) / (zFar - zNear);
	}

	public void mul(final Mat4f m) {
		final float m00;
		final float m01;
		final float m02;
		final float m03;
		final float m10;
		final float m11;
		final float m12;
		final float m13;
		final float m20;
		final float m21;
		final float m22;
		final float m23;
		final float m30;
		final float m31;
		final float m32;
		final float m33;
		m00 = this.m00 * m.m00 + this.m01 * m.m10 + this.m02 * m.m20 + this.m03 * m.m30;
		m01 = this.m00 * m.m01 + this.m01 * m.m11 + this.m02 * m.m21 + this.m03 * m.m31;
		m02 = this.m00 * m.m02 + this.m01 * m.m12 + this.m02 * m.m22 + this.m03 * m.m32;
		m03 = this.m00 * m.m03 + this.m01 * m.m13 + this.m02 * m.m23 + this.m03 * m.m33;
		m10 = this.m10 * m.m00 + this.m11 * m.m10 + this.m12 * m.m20 + this.m13 * m.m30;
		m11 = this.m10 * m.m01 + this.m11 * m.m11 + this.m12 * m.m21 + this.m13 * m.m31;
		m12 = this.m10 * m.m02 + this.m11 * m.m12 + this.m12 * m.m22 + this.m13 * m.m32;
		m13 = this.m10 * m.m03 + this.m11 * m.m13 + this.m12 * m.m23 + this.m13 * m.m33;
		m20 = this.m20 * m.m00 + this.m21 * m.m10 + this.m22 * m.m20 + this.m23 * m.m30;
		m21 = this.m20 * m.m01 + this.m21 * m.m11 + this.m22 * m.m21 + this.m23 * m.m31;
		m22 = this.m20 * m.m02 + this.m21 * m.m12 + this.m22 * m.m22 + this.m23 * m.m32;
		m23 = this.m20 * m.m03 + this.m21 * m.m13 + this.m22 * m.m23 + this.m23 * m.m33;
		m30 = this.m30 * m.m00 + this.m31 * m.m10 + this.m32 * m.m20 + this.m33 * m.m30;
		m31 = this.m30 * m.m01 + this.m31 * m.m11 + this.m32 * m.m21 + this.m33 * m.m31;
		m32 = this.m30 * m.m02 + this.m31 * m.m12 + this.m32 * m.m22 + this.m33 * m.m32;
		m33 = this.m30 * m.m03 + this.m31 * m.m13 + this.m32 * m.m23 + this.m33 * m.m33;
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	public boolean invert() {
		final float m00 = this.m00;
		final float m01 = this.m01;
		final float m02 = this.m02;
		final float m03 = this.m03;
		final float m10 = this.m10;
		final float m11 = this.m11;
		final float m12 = this.m12;
		final float m13 = this.m13;
		final float m20 = this.m20;
		final float m21 = this.m21;
		final float m22 = this.m22;
		final float m23 = this.m23;
		final float m30 = this.m30;
		final float m31 = this.m31;
		final float m32 = this.m32;
		final float m33 = this.m33;
		this.m00 = m11 * m22 * m33 - m11 * m23 * m32 - m21 * m12 * m33 + m21 * m13 * m32 + m31 * m12 * m23 - m31 * m13 * m22;
		this.m10 = -m10 * m22 * m33 + m10 * m23 * m32 + m20 * m12 * m33 - m20 * m13 * m32 - m30 * m12 * m23 + m30 * m13 * m22;
		this.m20 = m10 * m21 * m33 - m10 * m23 * m31 - m20 * m11 * m33 + m20 * m13 * m31 + m30 * m11 * m23 - m30 * m13 * m21;
		this.m30 = -m10 * m21 * m32 + m10 * m22 * m31 + m20 * m11 * m32 - m20 * m12 * m31 - m30 * m11 * m22 + m30 * m12 * m21;
		this.m01 = -m01 * m22 * m33 + m01 * m23 * m32 + m21 * m02 * m33 - m21 * m03 * m32 - m31 * m02 * m23 + m31 * m03 * m22;
		this.m11 = m00 * m22 * m33 - m00 * m23 * m32 - m20 * m02 * m33 + m20 * m03 * m32 + m30 * m02 * m23 - m30 * m03 * m22;
		this.m21 = -m00 * m21 * m33 + m00 * m23 * m31 + m20 * m01 * m33 - m20 * m03 * m31 - m30 * m01 * m23 + m30 * m03 * m21;
		this.m31 = m00 * m21 * m32 - m00 * m22 * m31 - m20 * m01 * m32 + m20 * m02 * m31 + m30 * m01 * m22 - m30 * m02 * m21;
		this.m02 = m01 * m12 * m33 - m01 * m13 * m32 - m11 * m02 * m33 + m11 * m03 * m32 + m31 * m02 * m13 - m31 * m03 * m12;
		this.m12 = -m00 * m12 * m33 + m00 * m13 * m32 + m10 * m02 * m33 - m10 * m03 * m32 - m30 * m02 * m13 + m30 * m03 * m12;
		this.m22 = m00 * m11 * m33 - m00 * m13 * m31 - m10 * m01 * m33 + m10 * m03 * m31 + m30 * m01 * m13 - m30 * m03 * m11;
		this.m32 = -m00 * m11 * m32 + m00 * m12 * m31 + m10 * m01 * m32 - m10 * m02 * m31 - m30 * m01 * m12 + m30 * m02 * m11;
		this.m03 = -m01 * m12 * m23 + m01 * m13 * m22 + m11 * m02 * m23 - m11 * m03 * m22 - m21 * m02 * m13 + m21 * m03 * m12;
		this.m13 = m00 * m12 * m23 - m00 * m13 * m22 - m10 * m02 * m23 + m10 * m03 * m22 + m20 * m02 * m13 - m20 * m03 * m12;
		this.m23 = -m00 * m11 * m23 + m00 * m13 * m21 + m10 * m01 * m23 - m10 * m03 * m21 - m20 * m01 * m13 + m20 * m03 * m11;
		this.m33 = m00 * m11 * m22 - m00 * m12 * m21 - m10 * m01 * m22 + m10 * m02 * m21 + m20 * m01 * m12 - m20 * m02 * m11;
		float var18 = m00 * this.m00 + m01 * this.m10 + m02 * this.m20 + m03 * this.m30;
		if (var18 == 0.0F) {
			return false;
		}
		var18 = 1.0F / var18;
		this.m00 *= var18;
		this.m01 *= var18;
		this.m02 *= var18;
		this.m03 *= var18;
		this.m10 *= var18;
		this.m11 *= var18;
		this.m12 *= var18;
		this.m13 *= var18;
		this.m20 *= var18;
		this.m21 *= var18;
		this.m22 *= var18;
		this.m23 *= var18;
		this.m30 *= var18;
		this.m31 *= var18;
		this.m32 *= var18;
		this.m33 *= var18;
		return true;
	}

	public float[] toArray() {
		return new float[] {
			this.m00,
			this.m01,
			this.m02,
			this.m03,
			this.m10,
			this.m11,
			this.m12,
			this.m13,
			this.m20,
			this.m21,
			this.m22,
			this.m23,
			this.m30,
			this.m31,
			this.m32,
			this.m33,
		};
	}
}
