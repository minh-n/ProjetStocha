import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


enum InputFormat {TYPE1, TYPE2};

//plus je réfléchie moins la classe data à de sens
public class Data{

	protected File input_file;
	protected InputFormat format;

	//TODO trouver un type correc
	protected Object data;

	
	public Data(File f, InputFormat format)
	{
		this.input_file = f;
		this.format = format;
	}

	public Object read_input_file(InputFormat format)
	{
		//TODO stuff en fonction du format
		return "oui";
	}

}