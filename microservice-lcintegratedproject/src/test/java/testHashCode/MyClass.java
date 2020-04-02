package testHashCode;

import lombok.Data;

import java.util.HashSet;

/**
 * @author liuchaoOvO on 2020/4/2
 * @Description TODO
 */
public class MyClass {
    public static void main(String[] args) {
        HashSet set = new HashSet<>();
        set.add(new Student("1", "aa"));
        set.add(new Student("2", "aa"));
        set.add(new Student("1", "aa"));
        System.out.println("set:===" + set);

    }
}

@Data
class Student {
    String code;
    String name;

    @Override
    public int hashCode() {
        return code.hashCode() + name.hashCode();
    }
    public Student(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
