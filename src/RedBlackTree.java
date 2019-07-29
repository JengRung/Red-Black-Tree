import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * 
 * TODO RedBlackTree Insertion.
 *
 * @author tuj. Created Oct 17, 2018.
 * @param <T>
 */

public class RedBlackTree<T extends Comparable<T>> {

	public enum Color {
		RED, BLACK
	}

	public BinaryNode root;
	private int rotationNum;
	private int height;

	public RedBlackTree() {
		root = null;
		this.rotationNum = 0;
		this.height = this.height();
	}

	public boolean insert(T i) {
		if (i == null) {
			throw new IllegalArgumentException();
		}
		if (isEmpty()) {
			this.root = new BinaryNode(i);
			if (this.root.color == Color.RED) {
				this.root.colorFlip();

			}
			return true;
		}

		if (this.root.insert(i)) {
			if (this.root.color == Color.RED) {
				this.root.colorFlip();
			}
			return true;
		}
		if (this.root.color == Color.RED) {
			this.root.colorFlip();
		}
		return false;
	}

	public Boolean remove(T object) {
		if (object == null)
			throw new IllegalArgumentException();
		if (this.root == null) {
			return false;
		}
		if (this.root.getElement() == object && this.root.hasNoChild()) {
			this.root = null;
			return true;
		}
		RemoveHelper checkBox = new RemoveHelper(object);
		InsertHelper rotationBox = new InsertHelper();
		this.root = root.removeStep1(checkBox, rotationBox);
		if (this.root == null) {
			this.rotationNum += rotationBox.getRotationCount();
			return checkBox.getStatus();
		}
		this.root.color = Color.BLACK;// Step 4
		this.rotationNum += rotationBox.getRotationCount();
		return checkBox.getStatus();
	}

	public boolean isEmpty() {
		if (this.root == null) {
			return true;
		}
		return false;
	}

	public int height() {

		if (isEmpty())
			return -1;

		return this.root.adjustHeight();
	}

	public int size() {
		return this.size(this.root);
	}

	public int size(BinaryNode node) {
		if (node == null)
			return 0;
		return (size(node.leftChild) + 1 + size(node.rightChild));
	}

	public int getRotationCount() {
		return this.rotationNum;
	}

	public Iterator<RedBlackTree.BinaryNode> iterator() {
		return new PreOrderIterator(this);
	}

	public class BinaryNode {
		private T element;
		private BinaryNode leftChild;
		private BinaryNode rightChild;
		private BinaryNode parent;
		private Color color;

		public BinaryNode(T element) {
			this.element = element;
			this.leftChild = null;
			this.rightChild = null;
			this.parent = null;
			this.color = Color.RED;
		}

		// return an empty Node
		public BinaryNode() {
			this.leftChild = null;
			this.rightChild = null;
			this.element = null;
			this.color = Color.BLACK;
		}

		public boolean insert(T i) {

			BinaryNode newNode = new BinaryNode(i);
			int val = this.element.compareTo(i);
			boolean status = false;

			if (this.checkTwoRed() == true) {
				this.changeTwoRed();
				this.checkBalance();
			}

			if (val < 0) {
				if (this.rightChild == null) {
					this.rightChild = newNode;
					this.rightChild.parent = this;
					status = true;
					this.checkBalance();
					return status;
				}
				status = this.rightChild.insert(i);
			}

			else if (val > 0) {
				if (this.leftChild == null) {
					this.leftChild = newNode;
					this.leftChild.parent = this;
					status = true;
					this.checkBalance();
					return status;
				}
				status = this.leftChild.insert(i);
			}

			return status;
		}

		public void checkBalance() {
			if (this.color == Color.RED) {

				if (this.parent != null && this.parent.leftChild == this) {

					if (this.leftChild != null && this.leftChild.color == Color.RED) {
						// single right rotation
						this.parent.rotateRight();
						this.colorFlip();
					} else if (this.rightChild != null && this.rightChild.color == Color.RED) {
						// double right rotation
						this.parent.doubleRightRotation();
						this.colorFlip();
					}
				} else {
					if (this.rightChild != null && this.rightChild.color == Color.RED) {
						// single left rotation
						this.parent.rotateLeft();
						this.colorFlip();
					} else if (this.leftChild != null && this.leftChild.color == Color.RED) {
						// double left rotation
						this.parent.doubleLeftRotation();
						this.colorFlip();
					}
				}
			}
		}

		private void rotateRight() {
			rotationNum++;
			T rootElem = this.element;
			BinaryNode pivot = this.leftChild;
			BinaryNode pivotGC = pivot.leftChild;
			BinaryNode tempLC = this.rightChild;
			BinaryNode tempRGC = pivot.rightChild;
			this.element = (T) pivot.element;
			this.leftChild = pivotGC;
			this.rightChild = new BinaryNode(rootElem);
			this.rightChild.rightChild = tempLC;
			this.rightChild.leftChild = tempRGC;
			// update parent pointers
			if (this.leftChild != null)
				this.leftChild.parent = this;
			this.rightChild.parent = this;
			if (this.rightChild.rightChild != null)
				this.rightChild.rightChild.parent = this.rightChild;
			if (this.rightChild.leftChild != null)
				this.rightChild.leftChild.parent = this.rightChild;

		}

		private void rotateLeft() {
			rotationNum++;
			T rootElem = this.element;
			BinaryNode pivot = this.rightChild;
			BinaryNode pivotGC = pivot.rightChild;
			BinaryNode tempLC = this.leftChild;
			BinaryNode tempLGC = pivot.leftChild;
			this.element = (T) pivot.element;
			this.rightChild = pivotGC;
			this.leftChild = new BinaryNode(rootElem);
			this.leftChild.leftChild = tempLC;
			this.leftChild.rightChild = tempLGC;
			// update parent pointers
			if (this.rightChild != null)
				this.rightChild.parent = this;
			this.leftChild.parent = this;
			if (this.leftChild.leftChild != null)
				this.leftChild.leftChild.parent = this.leftChild;
			if (this.leftChild.rightChild != null)
				this.leftChild.rightChild.parent = this.leftChild;
		}

		private void doubleLeftRotation() {
			this.rightChild.rotateRight();
			this.rotateLeft();
		}

		private void doubleRightRotation() {
			this.leftChild.rotateLeft();
			this.rotateRight();
		}

		public void colorFlip() {
			if (this.color == Color.RED) {
				this.color = Color.BLACK;
				return;
			}
			this.color = Color.RED;
		}

		public boolean checkTwoRed() {
			if (this.rightChild != null && this.leftChild != null) {
				if (this.rightChild.color == Color.RED && this.leftChild.color == Color.RED) {
					return true;
				}
			}
			return false;
		}

		public void changeTwoRed() {
			this.colorFlip();
			this.leftChild.colorFlip();
			this.rightChild.colorFlip();
			if (this.parent != null) {
				this.parent.checkBalance();
			}
		}

		private BinaryNode removeStep1(RemoveHelper checkBox, InsertHelper rotationBox) {
			
			if (this.hasBothBlackChild()) {
				this.color = Color.RED;
				if (this.getElement().compareTo(checkBox.getRemoveElement()) == 0) {
					return this.removeStep3(checkBox, rotationBox);
				}
				return this.removeStep2(checkBox, rotationBox);
			}
			// step 1 case 2
			return this.removeStep2B(checkBox, rotationBox);

		}

		public BinaryNode removeStep2(RemoveHelper checkBox, InsertHelper rotationBox) {
			
			BinaryNode child = this.nextChild(checkBox);
			if (child == null) {
				return this;
			}
			if (child.hasBothBlackChild()) {
				return this.removeStep2A(checkBox, rotationBox);
			}
			BinaryNode a = null;
			a = child.removeStep2B(checkBox, rotationBox);
			if (child.getElement() == this.getLeftChild().getElement()) {
				this.setLeftChild(a);
			} else {
				this.setRightChild(a);
			}
			return this;

		}

		private BinaryNode removeStep2A(RemoveHelper checkBox, InsertHelper rotationBox) {
			
			int label = 0;// indicate left or right child
			BinaryNode child = this.nextChild(checkBox);
			BinaryNode sibling = null;
			BinaryNode parent = this;

			if (child.getElement() == this.leftChild.getElement()) {
			
				label = 1;
				sibling = this.rightChild;
			}
			if (child.getElement() == this.rightChild.getElement()) {
			
				label = 2;
				sibling = this.leftChild;
			}

			if (sibling.hasBothBlackChild()) {// Step2A1
			
				this.color = Color.BLACK;
				sibling.color = Color.RED;
				child.color = Color.RED;
			}

			if (label == 1 && sibling.getRightChild() != null && sibling.getRightChild().color == RedBlackTree.Color.RED
					|| label == 2 && sibling.getLeftChild() != null
							&& sibling.getLeftChild().color == RedBlackTree.Color.RED) {// Step2A3
		
				if (label == 1) {
					rotationBox.addSingleRotation();
					parent = this.remSingleLeftrotation();
				} else {
					rotationBox.addSingleRotation();
					parent = this.remSingleRightrotation();
				}
			}

			if (label == 1 && sibling.getLeftChild() != null && sibling.getLeftChild().color == RedBlackTree.Color.RED
					|| label == 2 && sibling.getRightChild() != null
							&& sibling.getRightChild().color == RedBlackTree.Color.RED) {// Step2A2
			
				if (label == 1) {
					rotationBox.addSingleRotation();
					rotationBox.addSingleRotation();
					parent = this.remDoubleLeftrotation();
				} else {
					rotationBox.addSingleRotation();
					rotationBox.addSingleRotation();
					parent = this.remDoubleRightrotation();
				}

			}

			if (child.getElement().compareTo(checkBox.getRemoveElement()) == 0) {
				if (label == 1) {
					this.setLeftChild(child.removeStep3(checkBox, rotationBox));
				} else if (label == 2) {
					this.setRightChild(child.removeStep3(checkBox, rotationBox));
				}

			} else {
				if (label == 1) {
					this.setLeftChild(child.removeStep2(checkBox, rotationBox));
				} else if (label == 2) {
					this.setRightChild(child.removeStep2(checkBox, rotationBox));
				}
			}
			return parent;

		}

		public BinaryNode removeStep2B(RemoveHelper checkBox, InsertHelper rotationBox) {
			
			if (this.getElement().compareTo(checkBox.getRemoveElement()) == 0) {

				return this.removeStep3(checkBox, rotationBox);
			}
			BinaryNode newNode = this.nextChild(checkBox);
			if (newNode == null) {

				return this;
			}
			if (newNode.color == RedBlackTree.Color.RED) {
				if (newNode == this.leftChild) {

					this.setLeftChild(newNode.removeStep2B1(checkBox, rotationBox));
				}
				if (newNode == this.rightChild) {

					this.setRightChild(newNode.removeStep2B1(checkBox, rotationBox));

				}
				return this;
			} else if (newNode.color == RedBlackTree.Color.BLACK) {
				int ind = 0;
				BinaryNode result = null;
				if (newNode == this.leftChild) {

					ind = 1;
					result = this.removeStep2B2(checkBox, rotationBox, ind);
					this.removeStep2(checkBox, rotationBox);

				}
				if (newNode == this.rightChild) {

					ind = 2;
					result = this.removeStep2B2(checkBox, rotationBox, ind);

					this.removeStep2(checkBox, rotationBox);
					
				}
				return result;
			}

			return null;
		}

		private BinaryNode removeStep2B2(RemoveHelper checkBox, InsertHelper rotationBox, int ind) {

			rotationBox.addSingleRotation();
			BinaryNode temp = null;
			if (ind == 1) {
				temp = this.getRightChild();
				temp.color = Color.BLACK;
				this.setRightChild(temp.getLeftChild());
				this.color = Color.RED;
				temp.setLeftChild(this);
			} else if (ind == 2) {
				temp = this.getLeftChild();
				temp.color = Color.BLACK;
				this.setLeftChild(temp.getRightChild());
				this.color = Color.RED;
				temp.setRightChild(this);
			} else if (ind == 0) {
				throw new IllegalStateException();
			}
			return temp;

		}

		private BinaryNode removeStep2B1(RemoveHelper checkBox, InsertHelper rotationBox) {

			BinaryNode child = this.nextChild(checkBox);
			if (this.getElement().compareTo(checkBox.getRemoveElement()) == 0) {
		
				return this.removeStep3(checkBox, rotationBox);
			}
			if (child == null) {// the node we need to remove is not here.
				return this;
			}
			return this.removeStep2(checkBox, rotationBox);

		}

		public BinaryNode removeStep3(RemoveHelper checkBox, InsertHelper rotationBox) {

			checkBox.setTrue();
			if (this.hasBothChild()) {// X has two children
	
				T i = this.leftChild.getLargest();
				switch (this.color) {
				case RED:
		
					checkBox.setRemoveElement(i);
					this.removeStep2(checkBox, rotationBox);
					this.setElement(i);
					return this;
				case BLACK:
				
					BinaryNode newParent = null;
					checkBox.setRemoveElement(i);
					newParent = this.removeStep2B(checkBox, rotationBox);
					this.setElement(i);
					return newParent;
				}

			} else if (this.hasNoChild()) {// X is a leaf.
				switch (this.color) {
				case RED:
					return null;
				case BLACK:
					throw new IllegalStateException();
				}

			} else if (this.getLeftChild() != null || this.getRightChild() != null) {// X
																						// has
																						// one
															
				if (null == this.leftChild) {
					this.setElement(this.getRightChild().getElement());
					this.setRightChild(null);
				} else {
					this.setElement(this.getLeftChild().getElement());
					this.setLeftChild(null);
				}
				return this;
			}

			throw new IllegalStateException();
		}

		private BinaryNode remSingleLeftrotation() {

			BinaryNode temp = this.getRightChild();
			this.setRightChild(temp.getLeftChild());
			temp.setLeftChild(this);
			temp.color = Color.RED;
			this.color = Color.BLACK;
			temp.rightChild.color = Color.BLACK;
			this.leftChild.color = Color.RED;
			return temp;

		}

		private BinaryNode remSingleRightrotation() {

			BinaryNode temp = this.getLeftChild();
			this.setLeftChild(temp.getRightChild());
			temp.setRightChild(this);
			temp.color = Color.RED;
			this.color = Color.BLACK;
			temp.leftChild.color = Color.BLACK;
			this.rightChild.color = Color.RED;
			return temp;

		}

		private RedBlackTree<T>.BinaryNode remDoubleRightrotation() {
			BinaryNode temp = this.getLeftChild();
			BinaryNode newParent = temp.getRightChild();
			this.color = Color.BLACK;
			this.rightChild.color = Color.RED;
			temp.setRightChild(newParent.getLeftChild());
			newParent.setLeftChild(temp);
			this.setLeftChild(newParent.getRightChild());
			newParent.setRightChild(this);
			return newParent;
		}

		private RedBlackTree<T>.BinaryNode remDoubleLeftrotation() {
			BinaryNode temp = this.getRightChild();
			BinaryNode newParent = temp.getLeftChild();
			this.color = Color.BLACK;
			this.leftChild.color = Color.RED;
			temp.setLeftChild(newParent.getRightChild());
			newParent.setRightChild(temp);
			this.setRightChild(newParent.getLeftChild());
			newParent.setLeftChild(this);
			return newParent;
		}

		private void setElement(T i) {
			this.element = i;
		}

		public int adjustHeight() {

			int LeftHeight = -1;
			int RightHeight = -1;

			if (this.leftChild != null) {
				LeftHeight = this.leftChild.adjustHeight();
			}
			if (this.rightChild != null) {
				RightHeight = this.rightChild.adjustHeight();
			}

			return 1 + Math.max(LeftHeight, RightHeight);

		}

		public void setRightChild(BinaryNode NRightChild) {
			this.rightChild = NRightChild;
		}

		public void setLeftChild(BinaryNode NLeftChild) {
			this.leftChild = NLeftChild;
		}

		public boolean hasBothChild() {
			if (this.leftChild != null && this.rightChild != null) {
				return true;
			}
			return false;
		}

		// Contain => true; not contain => false
		public boolean isContain(T cur) {
			int val = this.element.compareTo(cur);
			if (this == null) {
				return false;
			}
			if (this.element.equals(cur)) {
				return true;
			}
			if (val > 0) {
				if (this.leftChild == null)
					return false;
				return this.leftChild.isContain(cur);
			}
			if (val < 0) {
				if (this.rightChild == null)
					return false;
				return this.rightChild.isContain(cur);
			}
			return false;
		}

		public T getElement() {
			return element;
		}

		private T getLargest() {
			if (this.getRightChild() != null) {
				return this.rightChild.getLargest();
			}
			return this.getElement();
		}

		private BinaryNode nextChild(RemoveHelper checkBox) {
			int temp = this.element.compareTo(checkBox.getRemoveElement());
			BinaryNode nextChild = null;
			if (temp < 0) {
				nextChild = this.rightChild;
			} else if (temp > 0) {
				nextChild = this.leftChild;
			} else if (temp == 0) {
				nextChild = this;
			}
			return nextChild;
		}

		public ArrayList<T> toArrayList(ArrayList a) {
			if (this.leftChild != null) {
				a = this.leftChild.toArrayList(a);
			}

			a.add(element);

			if (this.rightChild != null) {
				a = this.rightChild.toArrayList(a);
			}

			return a;
		}

		private boolean hasBothBlackChild() {
			if (this == null) {
				return false;
			}
			if (this.hasBothChild()) {
				if (this.getLeftChild().color == RedBlackTree.Color.BLACK
						&& this.getRightChild().color == RedBlackTree.Color.BLACK) {
					return true;
				}
				return false;
			}
			if (this.hasNoChild()) {
				return true;
			}
			if (this.getLeftChild() != null && this.getLeftChild().color == RedBlackTree.Color.BLACK) {
				return true;
			}
			if (this.getRightChild() != null && this.getRightChild().color == RedBlackTree.Color.BLACK) {
				return true;
			}
			return false;
		}

		public boolean hasNoChild() {
			if (this.getRightChild() == null && this.getLeftChild() == null) {
				return true;
			}
			return false;
		}

		public BinaryNode getLeftChild() {
			return this.leftChild;
		}

		public BinaryNode getRightChild() {
			return this.rightChild;
		}

		public Object getColor() {
			return this.color;
		}
	}

	private class PreOrderIterator implements Iterator {

		private Stack<RedBlackTree.BinaryNode> st;
		private RedBlackTree<T> RBT;
		private BinaryNode cur;

		public PreOrderIterator(RedBlackTree<T> RBT) {
			this.RBT = RBT;
			this.st = new Stack<RedBlackTree.BinaryNode>();
			if (this.RBT.root != null) {
				st.push(this.RBT.root);
			}
		}

		@Override
		public boolean hasNext() {
			return !st.isEmpty();
		}

		@Override
		public RedBlackTree.BinaryNode next() {
			if (st.isEmpty()) {
				throw new NoSuchElementException();
			}
			this.cur = st.pop();
			if (cur.rightChild != null) {
				st.push(cur.rightChild);
			}
			if (cur.leftChild != null) {
				st.push(cur.leftChild);
			}
			return cur;
		}
	}

	private class InsertHelper {
		private int rotationCount;

		public InsertHelper() {
			this.rotationCount = 0;
		}

		public void addSingleRotation() {
			this.rotationCount++;
		}

		public int getRotationCount() {
			return this.rotationCount;
		}
	}

	private class RemoveHelper {

		private boolean status;
		private T element;

		public RemoveHelper(T i) {
			this.status = false;
			this.element = i;
		}

		public void setFalse() {
			this.status = false;
		}

		public void setTrue() {
			this.status = true;
		}

		public void setRemoveElement(T i) {
			this.element = i;
		}

		public T getRemoveElement() {
			return this.element;
		}

		public boolean getStatus() {
			return this.status;
		}

	}
}
