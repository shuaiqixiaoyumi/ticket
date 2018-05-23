package cn.com.smart.test;

public class test {

	public static void main(String[] args) {
		String seqParentId ="null.ORG_U152087760156185209..ORG_U152087761249514055..ORG_U152087762224069735..ORG_U152087762270649861.";
		String[] bussinessGroupId2 = seqParentId.split("\\.\\.");
		System.out.println(bussinessGroupId2[1].replaceAll("\\.", ""));
	}
}
