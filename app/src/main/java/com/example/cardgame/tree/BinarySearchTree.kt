package com.example.cardgame.tree
import com.example.cardgame.methods.printToTerminal
import com.example.cardgame.struct.Node

class BinarySearchTree {
    var root: Node? = null
    private var staticCount : Int = 0

    private fun getNewNode(key: Int): Node {
        val n = Node()
        n.key = key
        n.count = 1
        n.left = null
        n.right = null
        return n
    }

    private fun insertNewNode(node: Node?, key: Int): Node {
        if (node == null) return getNewNode(key)
        if (key == node.key) {
            node.count++
        } else if (key < node.key) {
            node.left = insertNewNode(node.left, key)
        } else {
            node.right = insertNewNode(node.right, key)
        }
        return node
    }

    fun insert(key: Int) {
        if (root == null) root = getNewNode(key) else {
            insertNewNode(root, key)
        }
    }

    private fun inOrderTraversal(node: Node?, nodes: Array<Node?>) {
        if (node != null) {
            inOrderTraversal(node.left, nodes)
            nodes[staticCount++] = node
            inOrderTraversal(node.right, nodes)
        }
    }

    private fun printInOrder(node: Node?, isRoot: Boolean, isLeft: Boolean, isRight: Boolean) {
        if (node != null) {
            printInOrder(node.left, false, true, false)
            printToTerminal(
                "Node Key: ${node.key} numberOfInserts=${node.count} isRoot: ${isRoot} isleft: ${isLeft} isRight: ${isRight})")
            printInOrder(node.right, false, false, true)
        }
    }

    private fun printPreOrder(node: Node?, isRoot: Boolean, isLeft: Boolean, isRight: Boolean) {
        if (node != null) {
            printToTerminal(
                "Node Key: ${node.key} numberOfInserts=${node.count} isRoot: ${isRoot} isleft: ${isLeft} isRight: ${isRight})")
            printInOrder(node.left, false, true, false)
            printInOrder(node.right, false, false, true)
        }
    }

    private fun printPostOrder(node: Node?, isRoot: Boolean, isLeft: Boolean, isRight: Boolean) {
        if (node != null) {
            printInOrder(node.left, false, true, false)
            printInOrder(node.right, false, false, true)
            printToTerminal(
                "Node Key: ${node.key} numberOfInserts=${node.count} isRoot: ${isRoot} isleft: ${isLeft} isRight: ${isRight})")
        }
    }

    val sortedNodeList: Array<Node?>?
        get() {
            if (root == null) {
                return null
            }
            staticCount = 0
            val size = countNumberOfNodes(root)
            val nodes: Array<Node?> = arrayOfNulls<Node>(size)
            inOrderTraversal(root, nodes)
            return nodes
        }

    fun searchForNode(key: Int): Node? {
        var temp: Node?
        temp = root
        while (temp != null) {
            if(temp.key == key){return temp}
            else if (key < temp.key){temp = temp.left}
            else{temp = temp.right}
        }
        return null
    }

    private fun minValueNode(node: Node): Node? {
        var current: Node? = node
        while(current != null && current.left != null) {
            current = current.left
        }
        return current
    }

    private fun maxValueNode(node: Node?): Node? {
        var current: Node? = node
        while (current != null && current.right != null) {
            current = current.right
        }
        return current
    }

    private fun deleteNode(node: Node?, key: Int): Node? {
        var node: Node? = node ?: return null
        if (key < node!!.key) {node.left = deleteNode(node.left, key)}
        else if (key > node.key) {node.right = deleteNode(node.right, key)}
        else {
            val temp: Node?
            if (node.left == null) {
                temp = node.right
                //node = null
                return temp
            }
            else if (node.right == null) {
                temp = node.left
                //node = null
                return temp
            }
            temp = minValueNode(node)
            node.key = temp!!.key
            node.right = deleteNode(node.right, temp.key)
        }
        return node
    }

    fun delete(key: Int) {
        if (root == null) return
        deleteNode(root, key)
    }

    private fun buildBalancedTree(nodes: Array<Node?>, start: Int, end: Int): Node? {
        if (start > end) {
            return null
        }
        val mid = (start + end) / 2
        val current: Node? = nodes[mid]
        current!!.left = buildBalancedTree(nodes, start, mid - 1)
        current.right = buildBalancedTree(nodes, mid + 1, end)
        return current
    }

    private fun storeNodesToList(nodes: Array<Node?>, node: Node?) {
        if (node == null) {
            return
        }
        storeNodesToList(nodes, node.left)
        nodes[staticCount++] = node
        storeNodesToList(nodes, node.right)
    }

    fun balanceTree() {
        val nodes: Array<Node?> = arrayOfNulls<Node>(countNumberOfNodes(root))
        staticCount = 0
        storeNodesToList(nodes, root)
        //root = null;
        val n = nodes.size
        val newRoot: Node?
        newRoot = buildBalancedTree(nodes, 0, n - 1)
        root = null
        root = newRoot
    }

    private fun countNumberOfNodes(node: Node?): Int {
        return if (node == null) {
            0
        } else 1 + countNumberOfNodes(node.left) + countNumberOfNodes(node.right)
    }

    private fun balancedHeight(node: Node?): Int {
        if (node == null) return 0
        val leftSubTreeHeight = balancedHeight(node.left)
        if (leftSubTreeHeight == -1) {
            return -1
        }
        val rightSubTreeHeight = balancedHeight(node.right)
        if (rightSubTreeHeight == -1) {
            return -1
        }
        return if (Math.abs(leftSubTreeHeight - rightSubTreeHeight) > 1) {
            -1
        } else Math.max(leftSubTreeHeight, rightSubTreeHeight) + 1
    }

    fun isBalanced(): Boolean{
        return balancedHeight(root) != -1
    }

    fun itemExist(key:Int):Boolean{
        return searchForNode(key) != null
    }
}