if (file.equals("LIST"))
{

	boolean printList = false;

	if (dataList.isEmpty())
	{

	}
	else
	{
		for (int i = 0; i < dataList.size(); i++)
		{
			
			//Print Data in format

		}

		printList = true;
		break;

	}

	if (printList == true)
	{
		os.println("200 OK");
	}
	else
	{
		os.println("List cannot be printed");
	}
}
