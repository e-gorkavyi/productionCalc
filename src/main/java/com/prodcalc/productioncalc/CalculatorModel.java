package com.prodcalc.productioncalc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class CalculatorModel {
    private ArrayList<materialProperties> materials = new ArrayList<>();

    public ArrayList<String> materialNamesList = new ArrayList<>();
    public ProductTypes boxType = ProductTypes.F0201;
    public String selectedMaterial;
    public int productLength = 100;
    public int productWidth = 100;
    public int productHeight = 100;
    public boolean printOn = false;
    public boolean handlerOn = false;
    public ObservableList<String> materialViewList;
    String cutTimeTxt = "";
    String sheetSizeTxt = "";
    String partSizeTxt = "";
    String partOnSheetTxt = "";
    String priceTxt = "";

    public CalculatorModel() {
        materials.add(new materialProperties(
                "5-сл, профиль BE",
                5F,
                1700F,
                1400F,
                177F,
                3.5F,
                1.5F,
                "W"
        ));
        materials.add(new materialProperties(
                "3-сл, профиль C",
                4F,
                2200F,
                1400F,
                160F,
                2.5F,
                1.5F,
                "W"
        ));
        materials.add(new materialProperties(
                "микрогофрокартон, профиль E",
                2F,
                2200F,
                1400F,
                140F,
                1.3F,
                0.7F,
                "L"
        ));

        selectedMaterial = materials.get(0).name;

        for (materialProperties mat : materials) {
            materialNamesList.add(mat.name);
        }

        materialViewList = FXCollections.observableArrayList(materialNamesList);
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
        materialProperties currentMaterial = materials.stream()
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
            float PT = 72F;     // стоимость резки
            float PP = 450;    // стоимость печати
            float PH = 20;     // стоимость ручки

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

class materialProperties {
    public String name;
    public float caliber;
    public float length;
    public float width;
    public float price;
    public float insideLoss;
    public float outsideGain;
    public String fluteDirection;

    public materialProperties(String name,
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