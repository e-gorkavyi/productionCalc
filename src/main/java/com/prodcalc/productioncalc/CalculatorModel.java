package com.prodcalc.productioncalc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class CalculatorModel {

    String matPrefsPath = System.getProperty(
            "user.home") + File.separator +
            ".config" + File.separator +
            "productionCalc" + File.separator +
            "materials.conf";

    String pricePrefsPath = System.getProperty(
            "user.home") + File.separator +
            ".config" + File.separator +
            "productionCalc" + File.separator +
            "prices.conf";

    public void matLoad(ArrayList<MaterialProperties> matList, String filePath) {
        Properties properties = new Properties();

        if (new File(filePath).exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(filePath);
                properties.loadFromXML(inputStream);

                int i = 0;
                while (true) {
                    if (properties.containsKey(String.valueOf(i))){
                        matList.add(new MaterialProperties(
                                properties.getProperty(String.valueOf(i)),
                                Float.parseFloat(properties.getProperty(i + "-caliber")),
                                Float.parseFloat(properties.getProperty(i + "-length")),
                                Float.parseFloat(properties.getProperty(i + "-width")),
                                Float.parseFloat(properties.getProperty(i + "-price")),
                                Float.parseFloat(properties.getProperty(i + "-insideLoss")),
                                Float.parseFloat(properties.getProperty(i + "-outsideGain")),
                                properties.getProperty(i + "-fluteDirection")
                        ));
                        i++;
                    } else {
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            materials.add(new MaterialProperties(
                    "5-сл, профиль BE",
                    5F,
                    1700F,
                    1400F,
                    177F,
                    3.5F,
                    1.5F,
                    "W"
            ));
            materials.add(new MaterialProperties(
                    "3-сл, профиль C",
                    4F,
                    2200F,
                    1400F,
                    160F,
                    2.5F,
                    1.5F,
                    "W"
            ));
            materials.add(new MaterialProperties(
                    "микрогофрокартон, профиль E",
                    2F,
                    2200F,
                    1400F,
                    140F,
                    1.3F,
                    0.7F,
                    "L"
            ));
        }
    }

    public void priceLoad() {
        Properties pricesProperties = new Properties();
        if (new File(pricePrefsPath).exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(pricePrefsPath);
                pricesProperties.loadFromXML(inputStream);

                cutPrice = Float.parseFloat(pricesProperties.getProperty("CutPrice"));
                printPrice = Float.parseFloat(pricesProperties.getProperty("PrintPrice"));
                handlerPrice = Float.parseFloat(pricesProperties.getProperty("HandlerPrice"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void matSave(ArrayList<MaterialProperties> matList, String matFilePath) {
        Properties properties = new Properties();

        try {
            for (int i = 0; i < matList.size(); i++) {
                properties.setProperty(String.valueOf(i), matList.get(i).name);
                properties.setProperty(i + "-caliber", String.valueOf(matList.get(i).caliber));
                properties.setProperty(i + "-length", String.valueOf(matList.get(i).length));
                properties.setProperty(i + "-width", String.valueOf(matList.get(i).width));
                properties.setProperty(i + "-price", String.valueOf(matList.get(i).price));
                properties.setProperty(i + "-insideLoss", String.valueOf(matList.get(i).insideLoss));
                properties.setProperty(i + "-outsideGain", String.valueOf(matList.get(i).outsideGain));
                properties.setProperty(i + "-fluteDirection", matList.get(i).fluteDirection);
            }

            File outFile = new File(matFilePath);
            outFile.getParentFile().mkdirs();
            System.out.println(outFile.delete());
            System.out.println(outFile.createNewFile());

            OutputStream outputstream = new FileOutputStream(outFile, false);
            properties.storeToXML(outputstream,"Material Prefs");
            outputstream.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(matFilePath);
        }

    }

    public void priceSave() {
        Properties pricesProperties = new Properties();
        pricesProperties.setProperty("CutPrice", String.valueOf(cutPrice));
        pricesProperties.setProperty("PrintPrice", String.valueOf(printPrice));
        pricesProperties.setProperty("HandlerPrice", String.valueOf(handlerPrice));

        try {
            File outFile = new File(pricePrefsPath);
            outFile.getParentFile().mkdirs();
            System.out.println(outFile.delete());
            System.out.println(outFile.createNewFile());

            OutputStream outputstream = new FileOutputStream(outFile, false);
            pricesProperties.storeToXML(outputstream, "Price Prefs");
            outputstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MaterialProperties getSelectedMaterial() {
        return materials
                .stream()
                .filter(mat -> selectedMaterial.equals(mat.name))
                .findAny()
                .orElse(null);
    }

    private ArrayList<MaterialProperties> materials = new ArrayList<>();

    public ArrayList<String> materialNamesList = new ArrayList<>();
    public ProductTypes boxType = ProductTypes.F0201;
    public String selectedMaterial;
    public int productLength = 100;
    public int productWidth = 100;
    public int productHeight = 100;
    public float cutPrice = 72;
    public float printPrice = 450;
    public float handlerPrice = 20;
    public boolean printOn = false;
    public boolean handlerOn = false;
    public ObservableList<String> materialViewList;
    String cutTimeTxt = "";
    String sheetSizeTxt = "";
    String partSizeTxt = "";
    String partOnSheetTxt = "";
    String priceTxt = "";

    public CalculatorModel() {

        matLoad(materials, matPrefsPath);

        selectedMaterial = materials.get(0).name;

        refreshMatList();

        materialViewList = FXCollections.observableArrayList(materialNamesList);
    }

    private void refreshMatList() {
        for (MaterialProperties mat : materials) {
            materialNamesList.add(mat.name);
        }
    }

    public boolean addMaterial (MaterialProperties material) {
        for (MaterialProperties mat : materials) {
            if (mat.name.equals(material.name))
                return false;
        }
        materials.add(material);
        materialNamesList.add(material.name);
        matSave(materials, matPrefsPath);
        return true;
    }

    public boolean deleteMaterial (MaterialProperties material) {
        for (MaterialProperties mat : materials) {
            if (mat.name.equals(material.name)) {
                materials.remove(mat);
                matSave(materials, matPrefsPath);
                return true;
            }
        }
        return false;
    }

    public boolean editMaterial (MaterialProperties sourceMat, MaterialProperties destMaterial) {
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).equals(sourceMat)) {
                materials.set(i, destMaterial);
                refreshMatList();
                matSave(materials, matPrefsPath);
                return true;
            }
        }
        return false;
    }
    
    private void sizeError() {
        cutTimeTxt = "";
        sheetSizeTxt = "";
        partSizeTxt = "";
        partOnSheetTxt = "";
        priceTxt = "Слишком большие размеры.";
    }

    private void numbersError() {
        cutTimeTxt = "";
        sheetSizeTxt = "";
        partSizeTxt = "";
        partOnSheetTxt = "";
        priceTxt = "Недопустимые размеры.";
    }

    public void go() {
        MaterialProperties currentMaterial = materials.stream()
                .filter(mat -> selectedMaterial.equals(mat.name))
                .findAny()
                .orElse(null);

        if (productLength == 0 || productWidth == 0 || productHeight == 0) {
            numbersError();
        } else if (currentMaterial != null) {
            float M = currentMaterial.caliber;
            float LMat = currentMaterial.length;
            float WMat = currentMaterial.width;
            float P = currentMaterial.price;
            float IL = currentMaterial.insideLoss;
            float OG = currentMaterial.outsideGain;

            // расчёт длины заготовки
            float BlanksOnProd = 1F;
            int LBlank = 0;
            int WBlank = 0;
            if (boxType == ProductTypes.F0201) {
                LBlank = Math.round(2 * (productLength + 2 * IL) + 2 * (productWidth + 2 * IL) + 45);
                if (LBlank <= LMat)
                    BlanksOnProd = 1F;
                if (Float.compare(LMat, LBlank) == -1 && Float.compare(LBlank, 2 * LMat) == -1)
                {
                    LBlank = Math.round((productLength + 2 * IL) + (productWidth + 2 * IL) + 45);
                    BlanksOnProd = 2;
                }
                if (LBlank >= (2 * LMat)) {
                    sizeError();
                    return;
                }
            }
            if (boxType == ProductTypes.F0427) {
                LBlank = (int) Math.round(productLength + 2 * IL + 2 *
                        (4 * M - OG + productHeight + 2 * IL + 2 * M + 2 * IL + productHeight + IL + M * 1.5));
                if (LBlank > LMat) {
                    sizeError();
                    return;
                }
            }
            if (boxType == ProductTypes.F0471) {
                LBlank = Math.round(productLength + 2 * IL + 2 * (2 * IL + M + productWidth + IL));
                if (LBlank > LMat) {
                    sizeError();
                    return;
                }
            }
            if (boxType == ProductTypes.BODY_LID) {
                LBlank = (int) Math.round(productLength + 2 * 
                        (2 * IL + M + productHeight + 2 * IL + productHeight + IL + 1.5 * M));
                if (LBlank > LMat) {
                    sizeError();
                    return;
                }
            }
            
            // расчёт ширины заготовки
            float WMax = WMat - 20;
            if (boxType == ProductTypes.F0201)
                WBlank = Math.round(productHeight + 2 * IL + 2 * M + productWidth + 2 * IL);
            if (boxType == ProductTypes.F0427)
                WBlank = Math.round(productHeight + productWidth + 2*IL + productHeight + 2*IL + productWidth + 2*IL + M + productHeight + M);
            if (boxType == ProductTypes.F0471)
                WBlank = Math.round(productHeight + productWidth + 2*IL + productHeight + 2*IL + productWidth + 2*IL + M + productHeight + M);
            if (boxType == ProductTypes.BODY_LID) {
                WBlank = (int) Math.round(productWidth + 2 * (2 * IL + M + productHeight + 2 * IL + productHeight + IL + 1.5 * M));
                if (WBlank > WMax) {
                    sizeError();
                    return;
                }
            }

            // расчёт количества заготовок на лист

            float CList = 1F;
            float C;
            float C1;
            float C2;

            // 1. без поворота
            float CL = LMat / LBlank;
            if (CL < 1) {
                CList = BlanksOnProd;
                CList = (float) Math.ceil(CList);
                CL = 1;
            }
            float CW = WMax / WBlank;
            CL = (float) Math.floor(CL);
            CW = (float) Math.floor(CW);
            if (boxType != ProductTypes.BODY_LID) {
                C1 = CL * CW;
            } else {
                C1 = (CL * CW) / 2;
            }

            // 2. с поворотом
            CL = LMat / WBlank;
            if (CL < 1) {
                CList = BlanksOnProd;
                CList = (float) Math.ceil(CList);
                CL = 1;
            }
            CW = WMax / LBlank;
            CL = (float) Math.floor(CL);
            CW = (float) Math.floor(CW);
            if (boxType != ProductTypes.BODY_LID)
                C2 = CL * CW;
            else
                C2 = (CL * CW) / 2;

            if (boxType != ProductTypes.F0201)
                C = Math.max(C1, C2);     // выбираем большее число заготовок
            else
                C = C1;              // для 0201 исключение

            int Smin = 0;
            int Smax = 0;
            float Tmin = 0F;
            float Tmax = 0F;

            // расчёт времени реза
            if (boxType == ProductTypes.F0201) {
                Smin = 112500;
                Smax = 2600000;
                Tmin = 1.2F;
                Tmax = 2.5F;
            }
            if (boxType == ProductTypes.F0427) {
                Smin = 250000;
                Smax = 2600000;
                Tmin = 2.2F;
                Tmax = 3.2F;
            }
            if (boxType == ProductTypes.F0471) {
                Smin = 150000;
                Smax = 2600000;
                Tmin = 1.6F;
                Tmax = 2.8F;
            }
            if (boxType == ProductTypes.BODY_LID) {
                Smin = 40000;
                Smax = 2600000;
                Tmin = 1.3F;
                Tmax = 3.2F;
            }

            int S = LBlank * WBlank;
            if (S < Smin) S = Smin;
            if (S > Smax) S = Smax;

            float T;

            if (boxType == ProductTypes.F0201) {
                T = (((S - Smin) * (Tmax - Tmin)) / (Smax - Smin)) + Tmin;
                T = T * BlanksOnProd;
            } else if (boxType == ProductTypes.BODY_LID) {
                T = ((((S - Smin) * (Tmax - Tmin)) / (Smax - Smin)) + Tmin) * 2;
            } else {
                T = (((S - Smin) * (Tmax - Tmin)) / (Smax - Smin)) + Tmin;
            }

            // Откугление до 0,5 минуты в плюс если превышение на 10 сек.
            int seconds = (int) ((T - (int)(T)) * 60);
            if (seconds > 10)
                T = (int) T + 0.5F;
            if (seconds > 40)
                T = (int) T + 1F;

            // Цена
            float PT = cutPrice;     // стоимость резки
            float PP = printPrice;    // стоимость печати
            float PH = handlerPrice;     // стоимость ручки

            double price = (P / C * BlanksOnProd + PT * T) * 1.5;

            // Добавляем стоимость печати
            if (printOn)
                price = price + ((double) LBlank / 1000) * ((double) WBlank / 1000) * PP * 1.5;

            // Добавляем стоимость ручки
            if (handlerOn)
                price = price + PH;

            cutTimeTxt = "Время реза: " + String.format("%.2f", T) + " мин.";
            sheetSizeTxt = "Лист: " + LMat + "x" + WMat;
            partSizeTxt = "Развёртка: " + LBlank + "x" + WBlank;
            partOnSheetTxt = "Изделий на листе: " + C / BlanksOnProd;
            priceTxt = "Стоимость " + String.format("%.2f", price) + " руб.";
        }
    }
}

class MaterialProperties {
    public String name;
    public float caliber;
    public float length;
    public float width;
    public float price;
    public float insideLoss;
    public float outsideGain;
    public String fluteDirection;

    public MaterialProperties(String name,
                              float caliber,
                              float length,
                              float width,
                              float price,
                              float insideLoss,
                              float outsideGain,
                              String fluteDirection) {
        this.name = name;
        this.caliber = caliber;
        this.length = length;
        this.width = width;
        this.price = price;
        this.insideLoss = insideLoss;
        this.outsideGain = outsideGain;
        this.fluteDirection = fluteDirection;
    }
}