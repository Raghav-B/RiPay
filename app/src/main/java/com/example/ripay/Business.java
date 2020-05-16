package com.example.ripay;

public class Business {
    static int numusers = 0;
    private int id;
    private String companyname;
    private double total_val;
    private double current_val;
    private int progress;



    //Perks
    private double bronzeval;
    private double silverval;
    private double goldval;

    public Business(String companyname, double total_val, double current_val,
                    double bronzeval, double silverval, double goldval) {

        this.id = numusers+1;
        numusers++;
        this.companyname = companyname;
        this.total_val = total_val;
        this.current_val = current_val;
        this.bronzeval = bronzeval;
        this.silverval = silverval;
        this.goldval = goldval;
        this.progress = (int) (current_val/total_val * 100);

    }

    public double getcur() {
        return this.current_val;
    }

    public int getprog() {
        return this.progress;
    }

    public double gettot() {
        return this.total_val;
    }

    public String getCompanyname() {
        return this.companyname;
    }

    public int getID() {
        return this.id;
    }

    public double getBronzeval(){
        return this.bronzeval;
    }

    public double getSilverval(){
        return this.silverval;
    }

    public double getGoldval(){
        return this.goldval;
    }

   public double[] getArr() {
        double[] arr = new double[6];
        arr[0] = this.id;
        arr[1] = this.total_val;
        arr[2] = this.current_val;
        arr[3] = this.progress;
        arr[4] = this.bronzeval;
        arr[5] = this.silverval;
        arr[6] = this.goldval;
        return arr;
   }


    @Override
    public String toString() {
        String str = "";

        if(this.companyname == "Done") {
            str = "No More Businesses";
        } else {

            str = String.format("%s: %.2f required, %.2f still needed",
                    this.companyname, this.total_val, this.current_val);

        }
        return str;
    }



}
