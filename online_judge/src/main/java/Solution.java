//class Solution {
//    public int[] twoSum(int[] nums, int target) {
//        for (int i = 0; i < nums.length; i++) {
//            for (int j = i + 1; j < nums.length; j++) {
//                if (nums[i] + nums[j] == target) {
//                    return new int[]{i, j};
//                }
//            }
//        }
//        return null;
//    }
//
//    public static void main(String[] args) {
//        Solution solution = new Solution();
//        int[] nums = new int[]{2, 7, 11, 15};
//        int target = 9;
//        int[] result = solution.twoSum(nums, target);
//        if (result.length == 2 && result[0] == 0 && result[1] == 1) {
//            System.out.println("success");
//        }else {
//            System.out.println("fail");
//        }
//    }
//}
class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

class Solution {
    public ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode node = head.next;
        head.next = null;
        while (node != null) {
            ListNode nextNode = node.next;
            head = addFist(head, node);
            node = nextNode;
        }
        return head;
    }

    public ListNode addFist(ListNode head, ListNode node) {
        if (head == null) {
            head = node;
            return null;
        }
        node.next = head;
        head = node;
        return head;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head = solution.reverseList(head);
        if (head.val == 4 && head.next.val == 3 && head.next.next.val == 2 && head.next.next.next.val == 1) {
            System.out.println("success");
        } else {
            System.out.println("fail");
        }
    }
}
