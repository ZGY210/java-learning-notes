# 0001. 两数之和（简单）

链接：https://leetcode.cn/problems/xxx/

---

## 一、思路

### 我自己先想到的（笨办法）

> ① 循环遍历
>
> ② 时间复杂度为O(n²)
>
> ③利用循环嵌套的方法,一个个遍历相加,与所要的值进行比较,以这个方法进行不算困难,但是代码的运行速度很慢,如果数据的复杂度提高,这个方法很难进行
> 这段别嫌丢人。面试时被问「你怎么想的」，答案就是这一段。

### 更好的做法

> 用 HashMap 把『找另一个数』从 O(n) 降到 O(1)」。

### 为什么它能成立

> 优解搞得还不是很懂

---

## 二、复杂度

| 版本  | 时间    | 空间   |
| --- | ----- | ---- |
| 笨办法 | O(n²) | O(1) |
| 更优解 | O(n)  | O(n) |

推导过程（不要只写结果）：

- 时间：……
- 空间：……

---

## 三、代码

### 版本一：能过的（笨办法）

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int j,i;
        for(i=0;i<nums.length;i++){
            for(j=i+1;j<nums.length;j++){
                if(nums[i]+nums[j]==target){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{0};
    }
}
```

### 版本二：更优的

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> hashtable = new HashMap<Integer, Integer>();
        for (int i = 0; i < nums.length; ++i) {
            if (hashtable.containsKey(target - nums[i])) {
                return new int[]{hashtable.get(target - nums[i]), i};
            }
            hashtable.put(nums[i], i);
        }
        return new int[0];
    }
}
```

---

## 四、踩坑

> 这一栏是四段里**最值钱**的。面试官问「你踩过什么坑」时，答案从这里来。

- [ ] 坑 1：写成了 ……，结果是 ……，原因是 ……
- [ ] 坑 2：……

---

## 五、一周后重做记录

| 日期  | 是否独立做出 | 用时  | 备注  |
| --- |:------:| --- | --- |
|     |        |     |     |
