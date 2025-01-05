class Solution {
    public int[] twoSum(int[] nums, int target) {
        for(int i = 0; i < nums.length; i++){
            for(int j = i+1; j < nums.length; j++){
                if(nums[i] + nums[j] == target){
                    int[] result = {i,j};
                    return result;
                }
            }
        }
        return null;
    }
public static void main(String[] args) {
        Solution solution = new Solution();
        int[] nums = new int[]{2, 7, 11, 15};
        int target = 9;
        int[] result = solution.twoSum(nums, target);
        if (result.length == 2 && result[0] == 0 && result[1] == 1) {
            System.out.println("success");
        }else {
            System.out.println("fail");
        }
    }
}