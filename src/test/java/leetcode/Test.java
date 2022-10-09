package leetcode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Test {


    public static void main(String[] args) {
        new Test().threeSumClosest(new int[]{0,0,0},1);
    }

    public int scoreOfParentheses(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        for(int i = 0;i < s.length();i++){
            if(s.charAt(i) == '('){
                stack.push(0);
            }else{
                int val = stack.pop();
                int top = stack.pop() + Math.max(2 * val,1);
                stack.push(top);
            }
        }
        return stack.peek();
    }

    public String reformatNumber(String number) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i < number.length();i++){
            if(number.charAt(i) <= '9' && number.charAt(i) >= '0'){
                sb.append(number.charAt(i));
            }
        }

        StringBuilder res = new StringBuilder();

        int n = sb.length();
        int index = 0;
        while(n > 0){
            if(n > 4){
                res.append(sb.substring(index,index + 3) + '-');
                index += 3;
                n -= 3;
            }else{
                if(n == 4){
                    res.append(sb.substring(index,index + 2) + '-' + sb.substring(index + 2,index + 4));
                }else{
                    res.append(sb.substring(index,index + n));
                }
                break;
            }
        }
        return res.toString();

    }

    public int maxAscendingSum(int[] nums) {
        int res = nums[0];
        int sum = nums[0];
        for(int i = 1;i < nums.length;i++){
            if(nums[i] > nums[i - 1]){
                sum += nums[i];
            }else{
                sum = nums[i];
            }
            res = Math.max(res,sum);
        }
        return res;
    }


//    public int[] advantageCount(int[] nums1, int[] nums2) {
//        int n = nums1.length;
//        Integer[] idx1 = new Integer[n];
//        Integer[] idx2 = new Integer[n];
//
//        for(int i = 0;i < n;i++){
//            idx1[i] = i;
//            idx2[i] = i;
//        }
//
//        Arrays.sort(idx1,(i,j) -> nums1[i] - nums1[j]);
//        Arrays.sort(idx2,(i,j) -> nums2[i] - nums2[j]);
//
//        int[] res = new int[n];
//        int left = 0,right = n - 1;
//        for(int i = 0;i < n;i++){
//            if(nums1[idx1[i]] > nums2[idx2[left]]){
//                res[idx2[left]] = nums1[idx1[i]];
//                left++;
//            }else{
//                res[idx2[right]] = nums1[idx1[i]];
//                right--;
//            }
//        }
//        return res;
//    }

    private  int[] list;// 存入nums1
    private boolean[] isVisited; //方便删除元素
    public int[] advantageCount(int[] nums1, int[] nums2) {// 主函数
        Arrays.sort(nums1);
        list = new int[nums1.length];
        isVisited = new boolean[nums1.length];
        for(int i=0;i<nums1.length;i++) list[i] = nums1[i];
        for(int i = nums1.length - 1;i >= 0;i--){
            int index = binarySearch(nums2[i]);
            nums1[i] = list[index];// 找到了nums2[i]的上确界或者最小值
            isVisited[index] = true;// 相当于删除这个被用掉的数字
        }
        return nums1;
    }
    public int binarySearch(int val){
        int left = 0,right = list.length - 1;
        while(left <= right){// 闭区间 ，<= 最后left就是val要插入的位置，也就是val的上确界
            int mid = (left + right)/2;
            if(list[mid] <= val){// 向右边查找
                left = mid +1;
            }else{
                right = mid - 1;
            }
        }
        while(left < list.length && isVisited[left] == true) left++;
        if(left >= list.length) left = 0;// 没在nums1剩余元素中找到nums2[i]的上确界 直接填入最小值
        while(left < list.length && isVisited[left] == true) left++;// 寻找找到没被使用的最小值
        return left;
    }

    public int numDecodings(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        for(int i = 1;i < n + 1;i++){
            if(s.charAt(i - 1) != '0'){
                dp[i] = dp[i - 1];
            }
            if(i > 1 && s.charAt(i - 2) != '0' && ((s.charAt(i - 2) - '0') * 10 + s.charAt(i - 1) - '0') <= 26){
                dp[i] += dp[i - 2];
            }
        }

        return dp[n];
    }



    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int[] nums = new int[m + n];
        int p1 = 0,p2 = 0;
        for(int i = 0;i < m + n;i++){
            if(p1 == m || p2 == n){
                if(p1 == m){
                    nums[i] = nums2[p2];
                    p2++;
                }else{
                    nums[i] = nums1[p1];
                    p1++;
                }
            }else if(nums1[p1] > nums2[p2]){
                nums[i] = nums2[p2];
                p2++;
            }else{
                nums[i] = nums1[p1];
                p1++;
            }
        }

        for(int i = 0;i < m + n;i++){
            nums1[i] = nums[i];
        }
    }


    public boolean isFlipedString(String s1, String s2) {
        if(s1.length() != s2.length()) return false;
         int count = 0,k = 0;
         int n = s1.length();
         for(int i = 0;i < 2 * n;i++){
             if(count == n) return true;
             if(s2.charAt(i % n) == s1.charAt(k)){
                 count++;
                 k++;
             }else{
                 count = 0;
                 k = 0;

             }
         }
         return false;
    }



    int[][] used;

    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        boolean[][] visited = new boolean[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                boolean flag = check(board, visited, i, j, word, 0);
                if (flag) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean check(char[][] board, boolean[][] visited, int i, int j, String s, int k) {
        if(s.charAt(k) != board[i][j]){
            return false;
        }else if(k == s.length() - 1){
            return true;
        }

        visited[i][j] = true;
        boolean result = false;
        int[][] diretions =  {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for(int[] diretion : diretions){
            int newi = i + diretion[0],newj = j + diretion[1];
            if(newi >= 0 && newi < board.length && newj >= 0 && newj < board[0].length){
                if(!visited[newi][newj]){
                    if(check(board,visited,newi,newj,s,k + 1)){
                        result = true;
                        break;
                    }
                }

            }
        }
        visited[i][j] = false;
        return result;
    }



    public int getKthMagicNumber(int k) {
        int[] dp = new int[k];
        dp[0] = 1;
        int p3 = 0,p5 = 0,p7 = 0;
        for(int i = 1;i < k;i++){
            int v1 = dp[p3] * 3,v2 = dp[p5] * 5,v3 = dp[p7] * 7;
            int min = Math.min(v1,Math.min(v2,v3));
            if(min == v1) p3++;
            if(min == v2) p5++;
            if(min == v3) p7++;
            dp[i] = min;
        }

        return dp[k - 1];

    }

    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int n = nums.length;
        int best = 10000000;

        for(int i = 0;i < n;i++){
            if(i > 0 && nums[i] == nums[i - 1]) continue;
            int j = i + 1,k = n - 1;
            while(j < k){
                int sum = nums[i] + nums[j] + nums[k];
                if(sum == target) return target;
                if(Math.abs(sum - target) < Math.abs(best - target)){
                    best = sum;
                }
                if(sum > target){
                    int k0 = k - 1;
                    while(j < k0 && nums[k0] == nums[k]){
                        k0--;
                    }
                    k = k0;
                }else{
                    int j0 = j + 1;
                    while(j0 < k && nums[j0] == nums[j]){
                        j0++;
                    }
                    j = j0;
                }
            }
        }
        return best;
    }



    public int[] missingTwo(int[] nums) {
        int n = nums.length;
        boolean flag1 = false, flag2 = false;
        for(int i = 0;i < n;i++){
            if(nums[i] == n + 1) {
                flag1 = true;
            }
            else if(nums[i] == n + 2) {
                flag2 = true;
            }else{
                while(i + 1 != nums[i]){
                    int tmp = nums[i];
                    nums[i] = nums[nums[i] - 1];
                    nums[tmp - 1] = tmp;
                }
            }
        }

        int[] res = new int[2];

        int k = 0;
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) res[k++] = i + 1;
        }
        if (flag1 == false) res[k++] = n + 1;
        if (flag2 == false) res[k++] = n + 2;

        return res;
    }


}
