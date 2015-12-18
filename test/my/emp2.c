int main()
{
	int x=10,i=10,y;
	while(i>0)
	{
        i--;
		while (x>i)
		{
			x--;
			y=x;
		}
		x=x+10;
		if (x>=20)
			goto ERROR;
		if (x>=10)
			x=10;
		else
			goto ERROR;
	}
	return 0;
	ERROR:
	return 1;
}
