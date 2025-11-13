public class A{
    public static int aaa=0;
    C c=new C(10);
    int field1;
    int field2;
    private int privateField;

    public int getField(){
        return this.field1;
    }

    public int getPrivateField(int number){
        return privateField;
    }

    public void setPrivateField(int number){
        this.privateField=number;
    }
}//ATFD=0,ATLD=3