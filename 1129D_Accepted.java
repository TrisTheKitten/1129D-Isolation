import java.io.*;
import java.util.*;

public class Main {
    static final int MOD = 998244353;
    static final int MAX_N = 200005;
    static final int B = 300;

    static List<Integer>[] occurrences = new ArrayList[MAX_N];
    static int[] cnt = new int[MAX_N];
    static int[] dp = new int[MAX_N];
    static Bucket[] buckets = new Bucket[(MAX_N - 1) / B + 1];

    static class Bucket {
        int id, offset;
        int[] prefSum = new int[B];

        Bucket(int id) {
            this.id = id;
        }

        void rebuild() {
            int first = id * B, last = Math.min((id + 1) * B - 1, MAX_N - 1);
            int smallest = Integer.MAX_VALUE;
            for (int i = first; i <= last; i++) {
                smallest = Math.min(smallest, offset + cnt[i]);
            }
            for (int i = first; i <= last; i++) {
                cnt[i] -= smallest - offset;
            }
            offset = smallest;
            Arrays.fill(prefSum, 0);
            for (int i = first; i <= last; i++) {
                addSelf(prefSum, cnt[i], dp[i]);
            }
            for (int x = 1; x < B; x++) {
                addSelf(prefSum, x, prefSum[x - 1]);
            }
        }
    }

    static void addSelf(int[] arr, int idx, int val) {
        arr[idx] = (arr[idx] + val) % MOD;
    }

    static void add(int a, int b, int diff) {
        int bucketA = a / B, bucketB = b / B;
        if (bucketA == bucketB) {
            for (int i = a; i <= b; i++) {
                cnt[i] += diff;
            }
            buckets[bucketA].rebuild();
        } else {
            for (int i = a; i < (bucketA + 1) * B; i++) {
                cnt[i] += diff;
            }
            buckets[bucketA].rebuild();
            for (int i = bucketA + 1; i < bucketB; i++) {
                buckets[i].offset += diff;
            }
            for (int i = bucketB * B; i <= b; i++) {
                cnt[i] += diff;
            }
            buckets[bucketB].rebuild();
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int k = Integer.parseInt(st.nextToken());

        for (int i = 0; i <= n; i++) {
            occurrences[i] = new ArrayList<>();
            occurrences[i].add(-1);
        }
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket(i);
        }

        st = new StringTokenizer(br.readLine());
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = Integer.parseInt(st.nextToken());
        }

        dp[0] = 1;
        buckets[0].rebuild();

        for (int R = 0; R < n; R++) {
            int x = a[R];
            List<Integer> vec = occurrences[x];
            if (vec.size() >= 2) {
                add(vec.get(vec.size() - 2) + 1, vec.get(vec.size() - 1), -1);
            }
            add(vec.get(vec.size() - 1) + 1, R, 1);
            vec.add(R);

            int total = 0;
            for (int i = 0; i <= R / B; i++) {
                Bucket bucket = buckets[i];
                int atMost = k - bucket.offset;
                if (atMost >= 0) {
                    total = (total + bucket.prefSum[Math.min(atMost, B - 1)]) % MOD;
                }
            }

            dp[R + 1] = total;
            buckets[(R + 1) / B].rebuild();
        }

        System.out.println(dp[n]);
    }
}