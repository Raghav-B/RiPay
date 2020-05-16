package com.example.ripay;

public class Business {

    static int numUsers = 0;
    private int id;
    private String companyName;
    private double totalVal;
    private double currentVal;
    private int progress;

    //Perks
    private double bronzeVal;
    private double silverVal;
    private double goldVal;

    // Other details
    private int imageSrc;

    public Business(String companyName, double totalVal, double currentVal,
                    double bronzeVal, double silverVal, double goldVal,
                    int imageSrc) {
        this.id = numUsers + 1;
        numUsers++;
        this.companyName = companyName;
        this.totalVal = totalVal;
        this.currentVal = currentVal;
        this.bronzeVal = bronzeVal;
        this.silverVal = silverVal;
        this.goldVal = goldVal;
        this.progress = (int) (currentVal / totalVal * 100);

        this.imageSrc = imageSrc;
    }

    public double getCurVal() {
        return this.currentVal;
    }

    public int getProg() {
        return this.progress;
    }

    public double getTotalVal() {
        return this.totalVal;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public int getID() {
        return this.id;
    }

    public double getBronzeVal() {
        return this.bronzeVal;
    }

    public double getSilverVal() {
        return this.silverVal;
    }

    public double getGoldVal() {
        return this.goldVal;
    }

    public int getImageSrc() {
        return this.imageSrc;
    }

    public double[] getArr() {
        double[] arr = new double[6];
        arr[0] = this.id;
        arr[1] = this.totalVal;
        arr[2] = this.currentVal;
        arr[3] = this.progress;
        arr[4] = this.bronzeVal;
        arr[5] = this.silverVal;
        arr[6] = this.goldVal;
        return arr;
    }

    @Override
    public String toString() {
        String str = "";

        if (this.companyName.equals("Done")) {
            str = "No More Businesses";
        } else {
            str = String.format("%s: %.2f required, %.2f still needed",
                    this.companyName, this.totalVal, this.totalVal - this.currentVal);

        }
        return str;
    }


}
