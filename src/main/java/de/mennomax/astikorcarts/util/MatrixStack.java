package de.mennomax.astikorcarts.util;

import java.util.ArrayDeque;
import java.util.Deque;

public final class MatrixStack implements AutoCloseable {
	private final Deque<Mat4f> stack;

	public MatrixStack() {
		this.stack = new ArrayDeque<>();
		final Mat4f mat = new Mat4f();
		mat.makeIdentity();
		this.stack.addFirst(mat);
	}

	private Mat4f newMatrix() {
		return new Mat4f();
	}

	public MatrixStack push() {
		this.stack.addFirst(new Mat4f(this.matrix()));
		return this;
	}

	@Override
	public void close() {
		this.pop();
	}

	public MatrixStack pop() {
		this.stack.removeFirst();
		if (this.stack.isEmpty()) {
			throw new RuntimeException("Stack underflow");
		}
		return this;
	}

	public void makeIdentity() {
		this.matrix().makeIdentity();
	}

	public void translate(final float x, final float y, final float z) {
		final Mat4f mat = this.matrix();
		final Mat4f translation = this.newMatrix();
		translation.makeTranslation(x, y, z);
		mat.mul(translation);
	}

	public void rotate(final float angle, final float x, final float y, final float z) {
		final Mat4f mat = this.matrix();
		final Mat4f rotation = this.newMatrix();
		rotation.makeRotation(angle, x, y, z);
		mat.mul(rotation);
	}

	public void rotate(final Quat4f quat) {
		final Mat4f mat = this.matrix();
		final Mat4f rotation = this.newMatrix();
		rotation.makeQuaternion(quat);
		mat.mul(rotation);
	}

	public void scale(final float x, final float y, final float z) {
		final Mat4f mat = this.matrix();
		final Mat4f scale = this.newMatrix();
		scale.makeScale(x, y, z);
		mat.mul(scale);
	}

	public void perspective(final float fovy, final float aspect, final float zNear, final float zFar) {
		final Mat4f mat = this.matrix();
		final Mat4f scale = this.newMatrix();
		scale.makePerspective(fovy, aspect, zNear, zFar);
		mat.mul(scale);
	}

	public void mul(final Mat4f other) {
		final Mat4f mat = this.matrix();
		mat.mul(other);
	}

	public Mat4f matrix() {
		return this.stack.getFirst();
	}
}
