////////
import java.util.*;
import java.io.*;

////////
public class My_Tetris extends Tetris 
{
	public static int inf=(int)2e8;
	// enter your student id here
	public String id = new String("1600012819");

	public boolean[][] my_copy(boolean a_from[][])
	{
		boolean b_to[][];
		int a1=a_from.length;
		b_to=new boolean[a1][];
		for(int i=0;i<a1;i++)
		{
			int a2=a_from[i].length;
			b_to[i]=new boolean[a2];
			for(int j=0;j<a2;j++)
			{
				b_to[i][j]=a_from[i][j];
			}
		}
		return b_to;
	}

	public boolean my_cmp(boolean a[][], boolean b[][])
	{
		//4*4 piece
		
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				if(a[i][j]!=b[i][j])
					return false;
			}
		}
			
		return true;
	}
	
	public int tell_block_type(boolean tmp_block[][])
	{
		int block_type=-1;
		if(my_cmp(tmp_block,basic_block0)) 
			block_type=0;
		else if(my_cmp(tmp_block,basic_block1)) 
			block_type=1;
		else if(my_cmp(tmp_block,basic_block2)) 
			block_type=2;
		else if(my_cmp(tmp_block,basic_block3))
			block_type=3;
		else if(my_cmp(tmp_block,basic_block4))
			block_type=4;
		else if(my_cmp(tmp_block,basic_block5))
			block_type=5;
		else if(my_cmp(tmp_block,basic_block6))
			block_type=6;
		return block_type;
	}
	
	public boolean if_can_place(int block_x,int block_h,boolean block_shape[][],boolean no_piece_board[][])
	{
		//if this case can place on the board
		
		for(int dx=0;dx<4;dx++)
		{
			for(int dh=0;dh<4;dh++)
			{
				if(!block_shape[dh][dx])
					continue;
				//this box is true
				int box_x=block_x+dx;
				int box_h=block_h-dh;
				//no need to consider the left and right bound
				if(box_x<0||box_x>=w||box_h<0||box_h>=h) 
				{
					return false;
				}
				if(no_piece_board[box_h][box_x])
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public int get_final_h(int final_x,boolean block_shape[][],boolean no_piece_board[][])
	{
		int tmp_piece_h;
		int height=h-1;
		boolean height_found=false;
		for(int i=h;i>=0;i--)
		{
			if(height_found)
				break;
			//tell if the 4-width line is empty
			for(int j=final_x;j<final_x+4;j++)
			{
				if(i<0||i>h-1||j<0||j>w-1)
					continue;
				if(no_piece_board[i][j])
				{
					//not empty line
					height_found=true;
					height=i+1;
					break;
				}
			}
		}
		if(!height_found)
			height=0;
		
		//search which h can be placed on
		tmp_piece_h=height-1+4;
		while(tmp_piece_h>=0)
		{
			boolean can_drop=if_can_place(final_x,tmp_piece_h-1,block_shape,no_piece_board);
			if(can_drop)
				tmp_piece_h--;
			else
				break;
		}
		if(tmp_piece_h<0)
			tmp_piece_h=0;
		
		return tmp_piece_h;
	}
	
	//process possible board
	//see if can delete a line or game over
	//if return -1, this state will cause game over, very low score
	//return erode metric
	int update_possible_board(boolean possible_board[][],int final_x,int final_h,boolean rotate_shape[][]) 
	{
		//if game over
		for (int y = h-nBufferLines; y < h; y++) 
		{
			for (int x = 0; x < w; x++) 
			{
				if (possible_board[y][x]) // game over
					return -1;
			}
		}
		
		//not game over
		
		int delete_line_cnt=0;
		int delete_box_cnt=0;
		//convert to int matrix, 2 for the specific block
		int int_board[][]=new int[h][w];
		for(int board_h=0;board_h<h;board_h++)
			for(int board_x=0;board_x<w;board_x++)
			{
				if(possible_board[board_h][board_x])
					int_board[board_h][board_x]=1;
				else
					int_board[board_h][board_x]=0;
			}
		for(int dh=0;dh<4;dh++)
			for(int dx=0;dx<4;dx++)
			{
				if(rotate_shape[dh][dx])
				{
					int board_h=final_h-dh;
					int board_x=final_x+dx;
					int_board[board_h][board_x]=2;
				}
			}
		
		//try to delete line
		for (int y = 0; y < h-nBufferLines; y++) 
		{
			
			boolean full = true;
			for (int x = 0; x < w; x++) 
			{
				if (!possible_board[y][x]) 
				{
					full = false;
					break;
				}
			}
			if (full) 
			{
				//delete line
				delete_line_cnt++;
				for(int x=0;x<w;x++)
				{
					if(int_board[y][x]==2)
					{
						//of this rotate shape
						delete_box_cnt++;
					}
				}
				for (int i = y; i < h-nBufferLines; i++) 
				{
					for (int j = 0; j < w; j++) 
					{
						possible_board[i][j] = possible_board[i+1][j];
						possible_board[i+1][j] = false;
						int_board[i][j]=int_board[i+1][j];
						int_board[i+1][j]=0;
					}
				}
				y--;
			}
		}
		
		
		return delete_line_cnt*delete_box_cnt;
	}

	int get_landing_height(boolean possible_board[][])
	{
		//distance between piece and top
		int landing_height;
		int tmp_h=h-1;
		boolean h_determined=false;
		int board_h;
		while(tmp_h>=0)
		{
			
			//if this line is not empty, then height determined
			boolean if_empty=true;
			for(int x=0;x<w;x++)
			{
				if(possible_board[tmp_h][x])
				{
					h_determined=true;
					break;
				}
			}
			if(!h_determined)
				tmp_h--;
			else
				break;
		}
		if(h_determined)
			board_h=tmp_h+1;
		else
			board_h=0;
//		landing_height=h-board_h;
		landing_height=board_h;
		return landing_height;
	}
//	
//	int erode_metric(boolean possible_board[][])
//	{
//		int metric_val=0;
//		
//		return metric_val;
//	}
//	
	
	int get_row_transition(boolean possible_board[][])
	{
		int row_transition=0;
		for(int tmp_h=0;tmp_h<h-nBufferLines;tmp_h++)
		{
			boolean tmp_boolean=possible_board[tmp_h][0];
			for(int x=1;x<w;x++)
			{
				if(possible_board[tmp_h][x]!=tmp_boolean)
				{
					tmp_boolean=possible_board[tmp_h][x];
					row_transition++;
				}
			}
		}
		return row_transition;
	}
	
	int get_col_transition(boolean possible_board[][])
	{
		int col_transition=0;
		for(int tmp_x=0;tmp_x<w;tmp_x++)
		{
			boolean tmp_bool=possible_board[0][tmp_x];
			for(int tmp_h=1;tmp_h<h-nBufferLines;tmp_h++)
			{
				boolean this_bool=possible_board[tmp_h][tmp_x];
				if(this_bool!=tmp_bool)
				{
					tmp_bool=this_bool;
					col_transition++;
				}
			}
		}
		
		return col_transition;
	}
	
	int get_holes(boolean possible_board[][])
	{
		int hole_cnt=0;
		for(int tmp_x=0;tmp_x<w;tmp_x++)
		{
			int stripe_hole_cnt=0;
			boolean tmp_bool=true;
			for(int tmp_h=0;tmp_h<h-nBufferLines;tmp_h++)
			{
				boolean this_bool=possible_board[tmp_h][tmp_x];
				if(tmp_bool==true&&this_bool==false)
				{
					//begin of a possible hole stripe
					tmp_bool=false;
					stripe_hole_cnt=1;
				}
				else if(tmp_bool==false&&this_bool==false)
				{
					stripe_hole_cnt++;
				}
				else if(tmp_bool==false&&this_bool==true)
				{
					//end of a hole stripe
					tmp_bool=true;
					hole_cnt=hole_cnt+stripe_hole_cnt;
					stripe_hole_cnt=0;
				}
			}
		}
		
		return hole_cnt;
	}
	
	int summation(int x)
	{
		int val=(int)(1+x)*x/2;
		return val;
	}
	
	//if the box is in a well
	boolean if_in_well(int box_h,int box_x,boolean possible_board[][])
	{
		if(possible_board[box_h][box_x])
			return false;
		//this box is empty
		boolean left_wall=false;
		boolean right_wall=false;
		if(box_x==0)
		{
			left_wall=true;
		}
		else
		{
			if(possible_board[box_h][box_x-1])
				left_wall=true;
		}
		if(!left_wall)
			return false;
		if(box_x==w-1)
		{
			right_wall=true;
		}
		else
		{
			if(possible_board[box_h][box_x+1])
				right_wall=true;
		}
		if(!right_wall)
			return false;
		return true;
	}
	
	int get_well_metric(boolean possible_board[][])
	{
		int well_metric=0;
		for(int tmp_x=0;tmp_x<w;tmp_x++)
		{
			//search each column
			boolean tmp_in_well=false;
			int tmp_well_h=0;
			for(int tmp_h=0;tmp_h<=h-nBufferLines;tmp_h++)
			{
				boolean this_in_well=if_in_well(tmp_h,tmp_x,possible_board);
				if(tmp_in_well==false&&this_in_well==true)
				{
					tmp_in_well=true;
					tmp_well_h=1;
				}
				else if(tmp_in_well==true&&this_in_well==true)
				{
					tmp_well_h++;
				}
				else if(tmp_in_well==true&&this_in_well==false)
				{
					//end of a well
					int tmp_well_metric=summation(tmp_well_h);
					well_metric+=tmp_well_metric;
					tmp_in_well=false;
					tmp_well_h=0;
				}
			}
		}
		
		return well_metric;
	}
	

	
	int pd_evaluate(int final_x,int final_h,boolean rotate_shape[][],boolean no_piece_board[][])
	{
		
		boolean possible_board[][]=my_copy(no_piece_board);
		
		for(int dx=0;dx<4;dx++)
			for(int dh=0;dh<4;dh++)
			{
				if(rotate_shape[dh][dx])
				{
					int box_x=final_x+dx;
					int box_h=final_h-dh;
					
					
//					System.out.printf("final_x:%d final_h:%d\n",final_x,final_h);
//					System.out.println(Arrays.deepToString(rotate_shape));
//					System.out.printf("box_x:%d box_h:%d\n",box_x,box_h);
					possible_board[box_h][box_x]=true;
				}
			}
		
		int pd_val;
		//process possible_board, delete lines, game over, erode metric
		int erode_metric=update_possible_board(possible_board,final_x,final_h,rotate_shape);
//		System.out.printf("erode_metric:%d\n",erode_metric);
	
		if(erode_metric==-1)
		{
			//cause game over
			return -inf;
		}
		int landing_height=get_landing_height(possible_board);
		int row_transition=get_row_transition(possible_board);
		int col_transition=get_col_transition(possible_board);
		int holes=get_holes(possible_board);
		int well_metric=get_well_metric(possible_board);
//		System.out.printf("final_x:%d,",final_x);
//		System.out.printf("landing_height:%d,",landing_height);
//		System.out.printf("row_transition:%d,",row_transition);
//		System.out.printf("col_transition:%d,",col_transition);
//		System.out.printf("holes:%d,",holes);
//		System.out.printf("well_metric:%d,",well_metric);
		pd_val=-45*landing_height+34*erode_metric-32*row_transition-93*col_transition-79*holes-34*well_metric;
//		System.out.printf("pd_val:%d\n\n",pd_val);
		return pd_val;
	}

	
	
	// for each block, select its ultimate piece_x, piece_y and rotate shape
	//use flag to show if the ultimate state is determined, determine once for each block
	//six basic shape
	//each basic shape can rotate to 1, 2 or 4 shape
	//piece_x can be -1

	//if block is on the top, use flag to tell if ultimate state is determined
	//if block is not on the top, it must have been determined. set flag to false, which indicates 
	//that next block is not determined
	public boolean flag_top_determined=false;
	public int rotate_num=0;
	public int left_num=0;
	public int right_num=0;


	public boolean basic_block0[][]=new boolean[][]{{false,false,false,false},
													{ true, true, true, true},
													{false,false,false,false},
													{false,false,false,false}};
	public boolean basic_block1[][]=new boolean[][]{{false,false,false,false},
													{ true, true,false,false},
													{false, true, true,false},
													{false,false,false,false}};
	public boolean basic_block2[][]=new boolean[][]{{false,false,false,false},
													{false,false, true, true},
													{false, true, true,false},
													{false,false,false,false}};
	public boolean basic_block3[][]=new boolean[][]{{false,false,false,false},
													{ true, true, true,false},
													{ true,false,false,false},
													{false,false,false,false}};
	public boolean basic_block4[][]=new boolean[][]{{false,false,false,false},
													{false, true, true, true},
													{false,false,false, true},
													{false,false,false,false}};
	public boolean basic_block5[][]=new boolean[][]{{false,false,false,false},
													{ true, true, true,false},
													{false, true,false,false},
													{false,false,false,false}};
	public boolean basic_block6[][]=new boolean[][]{{false,false,false,false},
													{false, true, true,false},
													{false, true, true,false},
													{false,false,false,false}};

	//case 6  no need to rotate
	
	//if all rotate status are calculated
	public boolean flag_rotate_shape=false;
	//rotate status
	public boolean rotate_shape[][][][]=new boolean[6][4][4][4];

	//return a shape matrix 
	public boolean[][] get_rotate_shape(int block_type,int rotate_id)
	{
		boolean tmp_rotate_shape[][]=new boolean[4][4];
		if(block_type==6)
		{
			for(int i=0; i<4; i++)
				for(int j=0;j<4;j++)
				{
					tmp_rotate_shape[i][j]=basic_block6[i][j];
				}
		}
		else
		{
			boolean p_rotate_shape[][]=rotate_shape[block_type][rotate_id];
			for (int i=0;i<4;i++)
				for(int j=0;j<4;j++)
				{
					tmp_rotate_shape[i][j]=p_rotate_shape[i][j];
				}
		}
		return tmp_rotate_shape;
	}







	public PieceOperator robotPlay() 
	{
		// has_piece is true now

		//if there is no rotate_shape matrix, construct it
		if(!flag_rotate_shape)
		{
			for(int block_type=0;block_type<=5;block_type++)
			{
				boolean tmp_piece[][]=new boolean[4][4];
				boolean new_piece[][]=new boolean[4][4];
				boolean basic_piece[][]=new boolean[4][4];
				switch(block_type)
				{
					case 0:
						basic_piece=basic_block0;
						break;
					case 1:
						basic_piece=basic_block1;
						break;
					case 2:
						basic_piece=basic_block2;
						break;
					case 3:
						basic_piece=basic_block3;
						break;
					case 4:
						basic_piece=basic_block4;
						break;
					case 5:
						basic_piece=basic_block5;
						break;
				}
				for(int i=0;i<4;i++)
					for(int j=0;j<4;j++)
					{
						tmp_piece[i][j]=basic_piece[i][j];
						new_piece[i][j]=basic_piece[i][j];
					}
				for(int rotate_id=0;rotate_id<4;rotate_id++)
				{
					for(int i=0;i<4;i++)
						for(int j=0;j<4;j++)
						{
							tmp_piece[i][j]=new_piece[i][j];
						}
					for(int i=0;i<4;i++)
						for(int j=0;j<4;j++)
						{
							rotate_shape[block_type][rotate_id][i][j]=tmp_piece[i][j];
						}
					for (int y = 0; y < 4; y++) 
					{
						for (int x = 0; x < 4; x++) 
						{
							new_piece[y][x] = tmp_piece[x][3-y];
						}
					}
					
				}
			}
			flag_rotate_shape=true;
		}
		
		if(piece_y<h-1)
		{
			//piece not on the top
			//determined
			//next piece is not determined
			flag_top_determined=false;
			
			if(rotate_num>0)
			{
				rotate_num--;
				return PieceOperator.Rotate;		
			}
			if(left_num>0)
			{
				left_num--;
				return PieceOperator.ShiftLeft;
			}
			if(right_num>0)
			{
				right_num--;
				return PieceOperator.ShiftRight;
			}
			//no operation needed
			return PieceOperator.Drop;
		}
		else
		{
			//piece on the top
			if(flag_top_determined)
			{
				if(rotate_num>0)
				{
					rotate_num--;
					return PieceOperator.Rotate;		
				}
				if(left_num>0)
				{
					left_num--;
					return PieceOperator.ShiftLeft;
				}
				if(right_num>0)
				{
					right_num--;
					return PieceOperator.ShiftRight;
				}
				//no operation needed
				return PieceOperator.Drop;
			}
			else
			{
				//new piece, ultimate state not determined
				
				//which type
				int block_type=tell_block_type(piece);
				
				//to determine final state
				//search for each rotate shape and each final position
				//consider left and right bound
				
				int best_rotate_id=0;
				int best_final_x=piece_x;
//				int best_final_h=piece_y;
				int best_pd_val=-inf;
				
				for(int rotate_id=0;rotate_id<4;rotate_id++)
				{
					boolean rotate_shape[][]=get_rotate_shape(block_type,rotate_id);
					for(int final_x=-3;final_x<w;final_x++)
					{
						//left and right boundary detect
						boolean in_bound=true;
						for(int dx=0;dx<4;dx++)
						{
							if(!in_bound)
								break;
							for(int dh=0;dh<4;dh++)
							{
								if(!rotate_shape[dh][dx])
									continue;
								int box_x=final_x+dx;
								if(box_x<0||box_x>w-1)
								{
									in_bound=false;
									break;
								}
							}
						}
						//if not in bound, next search
						if(!in_bound)
							continue;
						//in boundary
						//find final h
						
						boolean no_piece_board[][]=my_copy(board);
						
						for(int tmp_h=h-nBufferLines;tmp_h<h;tmp_h++)
						{
							for(int tmp_x=0;tmp_x<w;tmp_x++)
								no_piece_board[tmp_h][tmp_x]=false;
						}
						
						int final_h=get_final_h(final_x,rotate_shape,no_piece_board);
						
					
						int pd_val=pd_evaluate(final_x,final_h,rotate_shape,no_piece_board);
						
						if(pd_val>best_pd_val)
						{
							best_pd_val=pd_val;
							best_rotate_id=rotate_id;
							best_final_x=final_x;
						}
					}
				}
				rotate_num=best_rotate_id;
				left_num=0;
				right_num=best_final_x-piece_x;
//				System.out.printf("rotate_num:%d\n",rotate_num);
//				System.out.printf("left_num:%d\n",left_num);
//				System.out.printf("right_num:%d\n",right_num);
				if(right_num<0)
				{
					left_num=-right_num;
					right_num=0;
					
				}
				
				flag_top_determined=true;
				
				if(rotate_num>0)
				{
					rotate_num--;
					return PieceOperator.Rotate;		
				}
				if(left_num>0)
				{
					left_num--;
				
					return PieceOperator.ShiftLeft;
				}
				if(right_num>0)
				{
					right_num--;
					return PieceOperator.ShiftRight;
				}
				//no operation needed
				return PieceOperator.Drop;
			}
			
		}

	}
}


