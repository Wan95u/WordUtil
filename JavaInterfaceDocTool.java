package com.cmcc.cmii.ambulance.common.utils;


import com.cmcc.cmii.ambulance.common.utils.WordDO.ClassDO;
import com.cmcc.cmii.ambulance.common.utils.WordDO.FunctionDO;
import com.cmcc.cmii.ambulance.common.utils.WordDO.ParamDO;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class JavaInterfaceDocTool {

    private static final List<ClassDO> result = new ArrayList<ClassDO>();

    public static void getAllFile(String filePath){
        File dir = new File(filePath);
        File[] files = dir.listFiles();
        if(files != null){
            for(int i=0;i<files.length;i++){
                String fileName = files[i].getName();
                if(files[i].isDirectory()){
                    getAllFile(files[i].getAbsolutePath());
                }else if(fileName.endsWith(".class")){
                    try{
                        String keyWord = "\\com\\";
                        String absPth = files[i].getCanonicalPath();
                        absPth = absPth.substring(absPth.indexOf(keyWord)+1,absPth.length()-6);
                        absPth = absPth.replace("\\",".");
                        Class c = Class.forName(absPth);
                        ClassDO co=new ClassDO();

                        if(c.isInterface()){
                            co.setClassName(c.getSimpleName());
                            Method[] methods = c.getDeclaredMethods();

                            List<FunctionDO> functionDOS=new ArrayList<FunctionDO>();

                            for(int j=0;j<methods.length;j++){
                                FunctionDO functionDO=new FunctionDO();
                                List<ParamDO> paramDOS=new ArrayList<ParamDO>();
                                //获取方法的名称
                                functionDO.setFunctionName(methods[j].getName().toString());
                                //获取参数的类型和名称
                                Parameter[] params=methods[j].getParameters();

                                for (Parameter class1:params) {
                                    ParamDO paramDO=new ParamDO();
                                    paramDO.setParamName(class1.getName());
                                    //截取最后一个.后的字符
                                    String typeName=class1.getType().toString();
                                    paramDO.setParamType(typeName.substring(typeName.lastIndexOf(".")+1));
                                    paramDOS.add(paramDO);
                                }
                                functionDO.setParam(paramDOS);
                                //获取返回值类型
                                String returnType=methods[j].getGenericReturnType().getTypeName().toString();
                                //若不包含List，截取最后一个.
                                if(returnType.indexOf("List")==-1){
                                    functionDO.setReturnType(returnType.substring(returnType.lastIndexOf(".")+1));
                                }else{
                                    String test=returnType.substring(returnType.lastIndexOf(".")+1);
                                    functionDO.setReturnType("List<"+test);
                                }

                                //将方法添加进方法集合中
                                functionDOS.add(functionDO);
                            }

                            co.setFunction(functionDOS);
                            result.add(co);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }




    public static void excelTool(List<ClassDO> result) throws IOException {
        XWPFDocument doc = new XWPFDocument();
        FileOutputStream out = new FileOutputStream(new File("D:\\api.docx"));
        XWPFParagraph p = null;
        XWPFRun r = null;

        for (ClassDO classDO : result) {
            //空两行
            p = doc.createParagraph();
            p = doc.createParagraph();
            r = p.createRun();
            r.setFontSize(20);
            r.setText("");
            p = doc.createParagraph();
            r = p.createRun();
            r.setFontSize(20);
            r.setText("");

            p.setAlignment(ParagraphAlignment.LEFT);
            r = p.createRun();
            r.setBold(true);
            r.setFontSize(20);
            r.setText("接口名称：");
            p = doc.createParagraph();
            p.setIndentationFirstLine(400);
            r = p.createRun();
            r.setFontSize(20);
            r.setText(classDO.getClassName());

            int totalRow = classDO.getFunction().size() + 1;
            int totalCol = 5;

            String[][] content = new String[totalRow][totalCol];
            content[0][0] = "方法名称";
            content[0][1] = "参数名称";
            content[0][2] = "参数类型";
            content[0][3] = "返回值类型";
            content[0][4] = "备注";

            for (int z = 0; z < classDO.getFunction().size(); z++) {
                FunctionDO functionDO = classDO.getFunction().get(z);

                List<ParamDO> params = functionDO.getParam();
                //方法名称
                content[z + 1][0] = functionDO.getFunctionName();
                //参数名称和类型
                content[z + 1][1]="";
                content[z + 1][2]="";
                if (params.size() > 0) {
                    for (int j = 0; j < params.size(); j++) {
                        content[z + 1][1] += params.get(j).getParamName() + ", ";
                        content[z + 1][2] += params.get(j).getParamType() + ", ";
                    }
                }
                //返回值类型
                content[z + 1][3] = functionDO.getReturnType();
            }
            //生成word中的表格
            XWPFTable table = doc.createTable(totalRow, totalCol);

            for(int i = 0; i < totalRow; i++) {
                XWPFTableRow row = table.getRow(i);
                CTTrPr trPr = row.getCtRow().addNewTrPr();
                CTHeight ht = trPr.addNewTrHeight();
                ht.setVal(BigInteger.valueOf(360L));
                List cells = row.getTableCells();

                for (int j = 0; j < totalCol; ++j) {
                    XWPFTableCell cell = row.getCell(j);

                    CTTcPr tcpr = cell.getCTTc().addNewTcPr();
                    CTVerticalJc va = tcpr.addNewVAlign();
                    va.setVal(STVerticalJc.CENTER);

                    CTShd ctshd = tcpr.addNewShd();
                    ctshd.setColor("auto");
                    ctshd.setVal(STShd.CLEAR);

                    if (i == 0) {
                        //设置表头颜色
                        ctshd.setFill("A7BFDE");
                    }

                    // 水平居中
                    cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.CENTER);
                    cell.setText(content[i][j]);
                }
            }
        }

        doc.write(out);
        out.close();
    }

    public static void main(String[] args) {
        String filePath = "D:\\EMSS-Cloud\\emss-background";
        try{
            getAllFile(filePath);
            excelTool(result);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



}

