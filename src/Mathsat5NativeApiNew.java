

public class Mathsat5NativeApiNew {

//  static {
//	System.setProperty( "java.library.path", "/home/gf/git/SAT_LA_CMP/lib/");
//    NativeLibraries.loadLibrary("mathsatj");
//  }

  public static void main(String[] args) {
//	  Api api = new Api();
	  
//    Mathsat5NativeApiNew api = new Mathsat5NativeApiNew();
    System.out.println(mathsat.api.msat_to_smtlib2_ext(10000L, 20000L, "dd", 1));
  }

}
