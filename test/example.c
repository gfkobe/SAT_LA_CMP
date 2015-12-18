int main()
{
	int x=50,i=0,y;
	while(i<50)
	{
		i++;
		while (x!=i)
		{
			x--;
			y=x;
		}
		x=x+60;
		if (x>100)
			goto ERROR;
		if (x>=50)
			x=50;
		else
			goto ERROR;
	}
	return 0;
	ERROR:
	return 1;
}
