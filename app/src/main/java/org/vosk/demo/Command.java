package org.vosk.demo;

import android.text.Editable;

public class Command {
    String phonetic;
    String name;
    ExecutableFunction func;


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

    boolean checkCommand(String input)
    {
        if (phonetic.equals(input)) return true;
        String[] inputWords = input.split(" ");
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
        }

        return false;
    }

}
