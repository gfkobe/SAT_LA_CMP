int main() 
{
	

	int a[5] = {0,1,0,3,4};
int *p, *q = &a[2];
p = a;// not support
p = p + 2;
*p = 2;
q = q + 1;
*q = 3;
//q = q - 1;
*q = 2;
if (a[2] == 0)
	goto ERROR;
	return 0;
ERROR:
	return 1;
}
