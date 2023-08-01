
    import java.util.LinkedList;
    import static org.junit.jupiter.api.Assertions.*;
    import org.junit.jupiter.api.Test;

    /**
     * Red-Black Tree implementation with a Node inner class for representing
     * the nodes of the tree. Currently, this implements a Binary Search Tree that
     * we will turn into a red black tree by modifying the insert functionality.
     * In this activity, we will start with implementing rotations for the binary
     * search tree insert algorithm. You can use this class' insert method to build
     * a regular binary search tree, and its toString method to display a level-order
     * traversal of the tree.
     */
    public class RedBlackTree<T extends Comparable<T>> {

        /**
         * This class represents a node holding a single value within a binary tree
         * the parent, left, and right child references are always maintained.
         */
        protected static class Node<T> {
            public int blackHeight; // field to track the black height for thr current node.
            public T data;
            public Node<T> parent; // null for root node
            public Node<T> leftChild;
            public Node<T> rightChild;
            public Node(T data) { this.data = data; }
            /**
             * @return true when this node has a parent and is the left child of
             * that parent, otherwise return false
             */
            public boolean isLeftChild() {
                return parent != null && parent.leftChild == this;
            }

        }

        protected Node<T> root; // reference to root node of tree, null when empty
        protected int size = 0; // the number of values in the tree

        /**
         * Performs a naive insertion into a binary search tree: adding the input
         * data value to a new node in a leaf position within the tree. After
         * this insertion, no attempt is made to restructure or balance the tree.
         * This tree will not hold null references, nor duplicate data values.
         * @param data to be added into this binary search tree
         * @return true if the value was inserted, false if not
         * @throws NullPointerException when the provided data argument is null
         * @throws IllegalArgumentException when the newNode and subtree contain
         *      equal data references
         */
        public boolean insert(T data) throws NullPointerException, IllegalArgumentException {
            // null references cannot be stored within this tree
            if(data == null) throw new NullPointerException(
                    "This RedBlackTree cannot store null references.");

            Node<T> newNode = new Node<>(data);
            if(root == null) { root = newNode; size++;
                root.blackHeight = 1;
                return true; } // add first node to an empty tree
            else{
                boolean returnValue = insertHelper(newNode,root); // recursively insert into subtree
                if (returnValue) size++;
                else throw new IllegalArgumentException(
                        "This RedBlackTree already contains that value.");
                root.blackHeight = 1;
                return returnValue;
            }
        }


        /**
         * Recursive helper method to find the subtree with a null reference in the
         * position that the newNode should be inserted, and then extend this tree
         * by the newNode in that position.
         * @param newNode is the new node that is being added to this tree
         * @param subtree is the reference to a node within this tree which the
         *      newNode should be inserted as a descenedent beneath
         * @return true is the value was inserted in subtree, false if not
         */
        private boolean insertHelper(Node<T> newNode, Node<T> subtree) {
            int compare = newNode.data.compareTo(subtree.data);
            // do not allow duplicate values to be stored within this tree
            if(compare == 0) return false;

                // store newNode within left subtree of subtree
            else if(compare < 0) {
                if(subtree.leftChild == null) { // left subtree empty, add here
                    subtree.leftChild = newNode;
                    newNode.parent = subtree;
                    enforceRBTreePropertiesAfterInsert(newNode);
                    return true;
                    // otherwise continue recursive search for location to insert
                } else return insertHelper(newNode, subtree.leftChild);
            }

            // store newNode within the right subtree of subtree
            else {
                if(subtree.rightChild == null) { // right subtree empty, add here
                    subtree.rightChild = newNode;
                    newNode.parent = subtree;
                    enforceRBTreePropertiesAfterInsert(newNode);
                    return true;
                    // otherwise continue recursive search for location to insert
                } else return insertHelper(newNode, subtree.rightChild);
            }
        }

        /**
         * Performs the rotation operation on the provided nodes within this tree.
         * When the provided child is a leftChild of the provided parent, this
         * method will perform a right rotation. When the provided child is a
         * rightChild of the provided parent, this method will perform a left rotation.
         * When the provided nodes are not related in one of these ways, this method
         * will throw an IllegalArgumentException.
         * @param child is the node being rotated from child to parent position
         *      (between these two node arguments)
         * @param parent is the node being rotated from parent to child position
         *      (between these two node arguments)
         * @throws IllegalArgumentException when the provided child and parent
         *      node references are not initially (pre-rotation) related that way
         */
        private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {

            if (child != parent.rightChild && child != parent.leftChild) {
                throw new IllegalArgumentException("Illegal Argument Exception thrown");
            }

            // case of right rotation
             if (child == parent.leftChild) {
                parent.leftChild = child.rightChild;
                if (child.rightChild != null) {
                    child.rightChild.parent = parent;
                }
                child.parent = parent.parent;
                child.rightChild = parent;

                //if parent's null
                if (parent.parent == null) {
                    root = child;
                }

                if (parent.parent != null) {
                    if (parent.parent.leftChild == parent) {
                        parent.parent.leftChild = child;
                    }
                    else if (parent.parent.rightChild == parent) {
                        parent.parent.rightChild = child;
                    }
                }
                parent.parent = child;
            }

            // case of left rotation
                if (child == parent.rightChild) {
                parent.rightChild = child.leftChild;

                if (child.leftChild != null) {
                    child.leftChild.parent = parent;
                }
                child.parent = parent.parent;
                child.leftChild = parent;

                if (parent.parent == null) {
                    root = child;
                }
                if (parent.parent != null) {
                    if (parent.parent.leftChild == parent) {
                        parent.parent.leftChild = child;
                    }
                    else {
                        parent.parent.rightChild = child;
                    }
                }
                parent.parent = child;
            }
        }
        /**
         * Get the size of the tree (its number of nodes).
         * @return the number of nodes in the tree
         */
        public int size() {
            return size;
        }

        /**
         * Method to check if the tree is empty (does not contain any node).
         * @return true of this.size() return 0, false if this.size() > 0
         */
        public boolean isEmpty() {
            return this.size() == 0;
        }

        /**
         * Checks whether the tree contains the value *data*.
         * @param data the data value to test for
         * @return true if *data* is in the tree, false if it is not in the tree
         */
        public boolean contains(T data) {
            // null references will not be stored within this tree
            if(data == null) throw new NullPointerException(
                    "This RedBlackTree cannot store null references.");
            return this.containsHelper(data, root);
        }

        /**
         * Recursive helper method that recurses through the tree and looks
         * for the value *data*.
         * @param data the data value to look for
         * @param subtree the subtree to search through
         * @return true of the value is in the subtree, false if not
         */
        private boolean containsHelper(T data, Node<T> subtree) {
            if (subtree == null) {
                // we are at a null child, value is not in tree
                return false;
            } else {
                int compare = data.compareTo(subtree.data);
                if (compare < 0) {
                    // go left in the tree
                    return containsHelper(data, subtree.leftChild);
                } else if (compare > 0) {
                    // go right in the tree
                    return containsHelper(data, subtree.rightChild);
                } else {
                    // we found it :)
                    return true;
                }
            }
        }


        /**
         * This method performs an inorder traversal of the tree. The string
         * representations of each data value within this tree are assembled into a
         * comma separated string within brackets (similar to many implementations
         * of java.util.Collection, like java.util.ArrayList, LinkedList, etc).
         * Note that this RedBlackTree class implementation of toString generates an
         * inorder traversal. The toString of the Node class class above
         * produces a level order traversal of the nodes / values of the tree.
         * @return string containing the ordered values of this tree (in-order traversal)
         */
        public String toInOrderString() {
            // generate a string of all values of the tree in (ordered) in-order
            // traversal sequence
            StringBuffer sb = new StringBuffer();
            sb.append("[ ");
            sb.append(toInOrderStringHelper("", this.root));
            if (this.root != null) {
                sb.setLength(sb.length() - 2);
            }
            sb.append(" ]");
            return sb.toString();
        }

        private String toInOrderStringHelper(String str, Node<T> node){
            if (node == null) {
                return str;
            }
            str = toInOrderStringHelper(str, node.leftChild);
            str += (node.data.toString() + ", ");
            str = toInOrderStringHelper(str, node.rightChild);
            return str;
        }

        /**
         * This method performs a level order traversal of the tree rooted
         * at the current node. The string representations of each data value
         * within this tree are assembled into a comma separated string within
         * brackets (similar to many implementations of java.util.Collection).
         * Note that the Node's implementation of toString generates a level
         * order traversal. The toString of the RedBlackTree class below
         * produces an inorder traversal of the nodes / values of the tree.
         * This method will be helpful as a helper for the debugging and testing
         * of your rotation implementation.
         * @return string containing the values of this tree in level order
         */
        public String toLevelOrderString() {
            String output = "[ ";
            if (this.root != null) {
                LinkedList<Node<T>> q = new LinkedList<>();
                q.add(this.root);
                while(!q.isEmpty()) {
                    Node<T> next = q.removeFirst();
                    if(next.leftChild != null) q.add(next.leftChild);
                    if(next.rightChild != null) q.add(next.rightChild);
                    output += next.data.toString();
                    if(!q.isEmpty()) output += ", ";
                }
            }
            return output + " ]";
        }

        public String toString() {
            return "level order: " + this.toLevelOrderString() +
                    "\nin order: " + this.toInOrderString();
        }

        /**
         *This method resolves any red-black tree property violations that are introduced when inserting a new node into
         *a red-black tree
         * @param newNode
         * @throws IllegalArgumentException
         */
        protected void enforceRBTreePropertiesAfterInsert(Node<T>newNode) throws IllegalArgumentException{
            if(newNode.equals(root.data)){   //checks if the newNode is the root
                newNode.blackHeight=1;       //makes the newNode black.
            }
            if(newNode.parent==null){       //checks if the parent of the newNode is null.
                return;                     //returns.
            }
            //case 1 - The new Node has a red parent node and a red uncle node.

            //if statements that makes sure, the parent, grandparent and uncle aren't null and are red, black and black
            //respectively.
            if (newNode.parent!=null &&newNode.parent.parent!=null && newNode.parent.parent.blackHeight==1 &&
                    newNode.parent.parent.leftChild!=null && newNode.parent.parent.leftChild.blackHeight == 0 &&
                    newNode.parent.parent.rightChild!=null && newNode.parent.parent.rightChild.blackHeight == 0) {

                //changes the grandparent's color to red, and the parent's and uncle's color to black.
                newNode.parent.parent.blackHeight=0;
                newNode.parent.parent.rightChild.blackHeight=1;
                newNode.parent.parent.leftChild.blackHeight=1;

                //calls the grandparent node recursively.
                enforceRBTreePropertiesAfterInsert(newNode.parent.parent);
            }

            //if statements that makes sure, the parent, grandparent and uncle aren't null and are red, black and black
            //respectively.
            if (newNode.parent!=null &&newNode.parent.parent!=null && newNode.parent.parent.blackHeight==1 &&
                    newNode.parent.parent.rightChild!=null && newNode.parent.parent.rightChild.blackHeight == 0 &&
                    newNode.parent.parent.leftChild!=null && newNode.parent.parent.leftChild.blackHeight == 0) {

                //changes the grandparent's color to red, and the parent's and uncle's color to black.
                newNode.parent.parent.blackHeight=0;
                newNode.parent.parent.rightChild.blackHeight=1;
                newNode.parent.parent.leftChild.blackHeight=1;

                //calls the grandparent node recursively.
                enforceRBTreePropertiesAfterInsert(newNode.parent.parent);

            }

            //case 2 - The new node has a red parent and a black uncle (line)

            //checks if the new Node is a right node and the parent is also a right node and the parent isn't null
            if(newNode.parent!=null&&!newNode.isLeftChild()&&!newNode.parent.isLeftChild()){

                //checks if the grandparent and uncle aren't null.
                //Also checks if the grandparent and uncle have a balckHeight of 1 and the parent's red.
                if(newNode.parent.parent!=null && newNode.parent.parent.blackHeight==1 &&
                        newNode.parent.blackHeight==0 && (newNode.parent.parent.leftChild==null||
                        newNode.parent.parent.leftChild.blackHeight==1)){

                    //swtiches the color of the parent and the grandparent.
                    newNode.parent.parent.blackHeight=0;
                    newNode.parent.blackHeight=1;

                    //rotates the parent and the grandparent.
                    rotate(newNode.parent, newNode.parent.parent);
                }}
            //checks if the newNode and the parent node are both right children and the parent isn't null.
            if(newNode.parent!=null&&newNode.isLeftChild()&&newNode.parent.isLeftChild()){

                //checks if the grandparent and uncle aren't null.
                //Also checks if the grandparent and uncle have a balckHeight of 1 and the parent's red.
                if(newNode.parent.parent!=null && newNode.parent.parent.blackHeight==1 &&
                        newNode.parent.parent.leftChild.blackHeight == 0 &&
                        (newNode.parent.parent.rightChild==null||newNode.parent.parent.rightChild.blackHeight==1)){

                    //swtiches the color of the parent and the grandparent.
                    newNode.parent.blackHeight=1;
                    newNode.parent.parent.blackHeight=0;

                    //rotates the parent and the grandparent.
                    rotate(newNode.parent, newNode.parent.parent);
                }}

            //case 3 - The new node has a red parent and a black uncle (triangle)

            //checks if the new Node is a left Child and the parent is a rightChild
            if(newNode.parent!=null&&newNode.isLeftChild()&&!newNode.parent.isLeftChild()){

                // checks if the parent, grandparent and uncle aren't null.
                // Also checks if the grandParent and Uncle have a blaclHeight of 1 and the parent's red.
                if(newNode.parent.parent!=null && newNode.parent.parent.blackHeight==1 &&
                        newNode.parent.parent.rightChild!=null && newNode.parent.parent.rightChild.blackHeight==0 &&
                        (newNode.parent.parent.leftChild==null|| newNode.parent.parent.leftChild.blackHeight==1)){

                    // rotates the newNode and the parentNode
                    rotate(newNode, newNode.parent);

                    //calls the parentNode (now the right child) recursively.
                    enforceRBTreePropertiesAfterInsert(newNode.rightChild);
                }}

            //checks if the new Node is a right Child and the parent is a left child
            if(newNode.parent!=null && !newNode.isLeftChild()&&newNode.parent.isLeftChild()){

                // checks if the parent, grandparent and uncle aren't null.
                // Also checks if the grandParent and Uncle have a blaclHeight of 1 and the parent's red.
                if(newNode.parent.parent!=null && newNode.parent.parent.blackHeight==1
                        && newNode.parent.parent.leftChild!=null &&
                        newNode.parent.parent.leftChild.blackHeight==0 &&
                        (newNode.parent.parent.rightChild==null|| newNode.parent.parent.rightChild.blackHeight==1)){

                    //rotates the newNode and the parent node
                    rotate(newNode, newNode.parent);

                    //recursively calls the parent node.
                    enforceRBTreePropertiesAfterInsert(newNode.leftChild);
                }}

        }

        /**
         * Checks the case1 of the EnforceRBT method. (The parent and uncle are red and the grandParent is black when
         * inserting)
         */
        @Test
        public void test1() {
            RedBlackTree<Integer> tree = new RedBlackTree<>();          //creating a new RedBlackTree object named tree.
            tree.insert(420);                                      //inserting node = 420
            assertEquals(420,tree.root.data);                   //checks if the 420 is the root
           assertEquals(1, tree.root.blackHeight);              //checks if the root has a blackHeight of 1

            //multiple insertions
            tree.insert(911);
            tree.insert(69);
            tree.insert(23);                                   //this insertion should give out a conflict- case1

          assertEquals(1, tree.root.blackHeight);  //checks if the root stays black after being changed to red.
          assertEquals(1, tree.root.leftChild.blackHeight);     //checks if the leftChild becomes black
          assertEquals(1, tree.root.rightChild.blackHeight);    //checks if the rightChild becomes black.

            //new RedBlackTree object
            RedBlackTree<Integer> tree2 = new RedBlackTree<>();

            //multiple insert statements
            tree2.insert(420);
            tree2.insert(69);
            tree2.insert(500);
            tree2.insert(600);

            //checks for the same tests as above, the only difference being that the newNode is a rightchild.
            assertEquals(420, tree2.root.data);
            assertEquals(1, tree2.root.blackHeight);
            assertEquals(500, tree2.root.rightChild.data);
            assertEquals(1, tree2.root.rightChild.blackHeight);
            assertEquals(69, tree2.root.leftChild.data);
            assertEquals(1, tree2.root.leftChild.blackHeight);
            }


        /**
         *Checks if the case 2 of the enforceRBT method works. (The grandParent and the uncle are black and the parent
         *is red. Also, both the parent and the new Node are either left children or right children)
         */
          @Test
          public void test2(){
              //creating a new RedBlackTree object named tree.
                RedBlackTree<Integer> tree = new RedBlackTree<>();

                //inserting nodes
                tree.insert(420);
              tree.insert(911);
              tree.insert(69);

              //changing the blackHeight of the rightChild to 1
              tree.root.rightChild.blackHeight=1;

              //inserting a new node that is a left child and which is gonna cause enforceRBT to run.
              tree.insert(23);

             //checks if the RedBlackTree is balanced and enforceRBT's case 2 works.
              //Also checks rotate as it is called in the enforceRBT method.
              assertEquals(69, tree.root.data);
              assertEquals(1, tree.root.blackHeight);
              assertEquals(23, tree.root.leftChild.data);
              assertEquals(0, tree.root.leftChild.blackHeight);
              assertEquals(420, tree.root.rightChild.data);
              assertEquals(0, tree.root.rightChild.blackHeight);
              assertEquals(911, tree.root.rightChild.rightChild.data);
              assertEquals(1, tree.root.rightChild.rightChild.blackHeight);

              // Creates another RedBlackTree object.
              RedBlackTree<Integer> tree2 = new RedBlackTree<>();

              //inserting nodes
              tree2.insert(420);
              tree2.insert(69);
              tree2.insert(666);

              //makes the leftChild black
              tree2.root.leftChild.blackHeight=1;

              //inserts a new red node as a right child and is going to cause enforceRBT to run
              tree2.insert(677);

              //checks if the RBT is balanced and if case 2 works.
              //Also checks rotate as it is called in the enforceRBT method.
              assertEquals(666, tree2.root.data);
              assertEquals(1, tree2.root.blackHeight);
              assertEquals(420, tree2.root.leftChild.data);
              assertEquals(0, tree2.root.leftChild.blackHeight);
              assertEquals(677, tree2.root.rightChild.data);
              assertEquals(0, tree2.root.rightChild.blackHeight);
              assertEquals(69, tree2.root.leftChild.leftChild.data);
              assertEquals(1, tree2.root.leftChild.leftChild.blackHeight);
            }

        /**
         *Checks if the case 3 of the enforceRBT method works. (The grandParent and the uncle are black and the parent
         *is red. Also, if the parent is the leftChild, the newNode is the rightChild and vice-versa.
         **/
        @Test
        public void Test3(){
            //creates a new RedBlackTree object.
              RedBlackTree<Integer>tree=new RedBlackTree<>();

              //inserting nodes.
              tree.insert(420);
              tree.insert(69);
              tree.insert(911);
              tree.insert(60);
              tree.insert(70);

              //changing the root's leftchild's rightchild's color to red.
              tree.root.leftChild.rightChild.blackHeight=1;

              //insert that's going to cause one of the RBTenforce method's if statement's to run.
              tree.insert(65);

              //checking if the red black tree is balanced after inserting the new node.
            assertEquals(420, tree.root.data);
            assertEquals(1, tree.root.blackHeight);
            assertEquals(65, tree.root.leftChild.data);
            assertEquals(1, tree.root.leftChild.blackHeight);
            assertEquals(60, tree.root.leftChild.leftChild.data);
            assertEquals(0, tree.root.leftChild.leftChild.blackHeight);
            assertEquals(69, tree.root.leftChild.rightChild.data);
            assertEquals(0, tree.root.leftChild.rightChild.blackHeight);
            assertEquals(70, tree.root.leftChild.rightChild.rightChild.data);
            assertEquals(1, tree.root.leftChild.rightChild.rightChild.blackHeight);

            //creating a second RBT object
            RedBlackTree<Integer>tree2=new RedBlackTree<>();

            // checks the same thing as above,  but for the oppoosite side
            tree2.insert(420);
            tree2.insert(666);
            tree2.insert(69);
            tree2.root.leftChild.blackHeight=1;
            tree2.insert(500);

            //checks if the RBT is balanced, implying that the method works.
            assertEquals(500,tree2.root.data);
            assertEquals(1,tree2.root.blackHeight);
            assertEquals(420, tree2.root.leftChild.data);
            assertEquals(0,tree2.root.leftChild.blackHeight);
            assertEquals(69, tree2.root.leftChild.leftChild.data);
            assertEquals(1,tree2.root.leftChild.leftChild.blackHeight);
            assertEquals(666, tree2.root.rightChild.data);
            assertEquals(0,tree2.root.rightChild.blackHeight);

            }

        /**
         * Checks all three cases to test recursive cases for the 2 cases that use recursion in the RBTenforce method.
         */
            @Test
        public void Test4(){
                //creating a new RBT object.
            RedBlackTree<Integer>tree=new RedBlackTree<>();

            //inserting nodes.
            tree.insert(420);
            tree.insert(69);
            tree.insert(666);
            tree.insert(23);
            tree.insert(20);
            tree.insert(30);
            tree.insert(80);
            tree.insert(70);
            tree.insert(75);

            //checking if the tree is kept balanced after inserting all these nodes.
                assertEquals(69, tree.root.data);
                assertEquals(1, tree.root.blackHeight);
                assertEquals(23, tree.root.leftChild.data);
                assertEquals(0, tree.root.leftChild.blackHeight);
                assertEquals(420, tree.root.rightChild.data);
                assertEquals(0, tree.root.rightChild.blackHeight);
                assertEquals(666, tree.root.rightChild.rightChild.data);
                assertEquals(1, tree.root.rightChild.rightChild.blackHeight);
                assertEquals(75, tree.root.rightChild.leftChild.data);
                assertEquals(1, tree.root.rightChild.leftChild.blackHeight);
                assertEquals(70, tree.root.rightChild.leftChild.leftChild.data);
                assertEquals(0, tree.root.rightChild.leftChild.leftChild.blackHeight);
                assertEquals(80, tree.root.rightChild.leftChild.rightChild.data);
                assertEquals(0, tree.root.rightChild.leftChild.rightChild.blackHeight);
                assertEquals(20, tree.root.leftChild.leftChild.data);
                assertEquals(1, tree.root.leftChild.leftChild.blackHeight);
                assertEquals(30, tree.root.leftChild.rightChild.data);
                assertEquals(1, tree.root.leftChild.rightChild.blackHeight);
            }

    }


