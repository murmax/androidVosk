package org.vosk.demo;

import android.text.Editable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Command {
    String phonetic;
    String name;
    ExecutableFunction func;


    public static class ParsedCommand {
        boolean isSure;
        double percentSure;
        Map<String, String> args;
        ParsedCommand(double percentSure, Map<String,String> args)
        {
            this.isSure = percentSure>=0.5;
            this.percentSure = percentSure;
            this.args = args;
        }
    }


    Command(String name, String phonetic,ExecutableFunction func)
    {
        this.name = name;
        this.phonetic = phonetic;
        this.func = func;
    }


    void execute()
    {
        if (func!=null)
        {
            func.exec();
        }
    }

    private static class ThreeStringSplit
    {
        String before;
        String argument;
        String after;
        ThreeStringSplit(String before,String argument, String after)
        {
            this.before = before;
            this.after = after;
            this.argument = argument;
        }
    }
    private ThreeStringSplit splitOnThreeString(String input) //Обработка конструкций типа "command %argument1% divider %argument2%"
    {
        if (!input.contains("%")) return new ThreeStringSplit(input,"","");
        int pos1 = input.indexOf('%'); //Позиция
        int pos2 = input.indexOf('%',input.indexOf('%')+1);
        if (pos1 == -1 ||
            pos2 == -1 ||
            input.substring(pos1+1,pos2).isEmpty())
        {
            return new ThreeStringSplit(input.replace("%%",""),"","");
        }
        return new ThreeStringSplit(
                input.substring(0,pos1).trim(),
                input.substring(pos1+1,pos2).trim(),
                input.substring(pos2+1).trim());

    }

    ParsedCommand checkCommand(String input)
    {


        if (phonetic.equals(input)) return new ParsedCommand(1, new HashMap<>());

        if (phonetic.contains("%"))
        {
            HashMap<String,String> args = new HashMap<>();
            ThreeStringSplit result = splitOnThreeString(phonetic);
            String temp = input.trim();
            int sureparts = 0;
            do { //По всей команде и ее аргументам
                if (temp.startsWith(result.before)) // должно быть верно чтобы команда оставалась верной
                {
                    sureparts += result.before.split(" ").length;
                    temp=temp.replaceFirst(result.before,"").trim();
                } else
                {
                    return new ParsedCommand(0, new HashMap<>());
                }
                if (result.after.isEmpty()) //Если аргумент в конце строки
                {
                    if (!result.argument.isEmpty()) {
                        args.put(result.argument, temp); //Добавляем все, что есть до конца
                    }
                    result = new ThreeStringSplit("","","");
                } else { //Если впереди еще что-то есть
                    ThreeStringSplit next = splitOnThreeString(result.after); //получаем следующий аргумент
                    int pos = temp.indexOf(next.before); //Точка, где начинается филлер следующего аргумента
                    if (pos!=-1) // Если команда верна и продолжается дальше
                    {
                        if (pos==0) // Если аргумент пуст, но команда верна
                        {
                            if (!result.argument.isEmpty()) {
                                args.put(result.argument, ""); //записываем пустой аргумент
                            }
                        } else
                        {
                            if (!result.argument.isEmpty()) {
                                args.put(result.argument, temp.substring(0, pos).trim()); // вырезаем аргумент и записываем его список аргументов
                            }
                            temp = temp.substring(pos); // вырезаем аргумент
                        }
                    }
                    result = next;
                }


            } while (!result.before.isEmpty());
            return new ParsedCommand(1, args);


        }

        /*String[] inputWords = input.split(" ");
        if (inputWords.length>1) {
            String[] phoneticWords = phonetic.split(" ");
            if (phoneticWords.length == 1) {
                for (String inputWord: inputWords) {
                    if (inputWord.equals(phoneticWords[0])) return true;
                }
            } else if (phoneticWords.length > 1)
            {
                int firstFound=-1;
                int firstFoundPosition=-1;


                for (int i=0;i<phoneticWords.length;i++){
                    for (int j=0;j<inputWords.length;j++)
                    {
                        if (phoneticWords[i].equals(inputWords[j])) {
                            firstFound = i;
                            firstFoundPosition = j;
                        }
                    }
                    if (firstFound!=-1) break;
                }
                if (firstFound != -1) {
                    int correct = 0;
                    int difference = firstFoundPosition - firstFound;
                    for (int i=firstFound; i<phoneticWords.length && i+difference<inputWords.length;i++){
                        if (phoneticWords[i].equals(inputWords[i+difference])) {
                            correct++;
                        }
                    }
                    if (correct*2>=phoneticWords.length) {
                        return true;
                    }
                }
            }
        }*/

        return new ParsedCommand(0, new HashMap<>());
    }

}
