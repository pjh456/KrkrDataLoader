from itertools import combinations
import re

class Onani:
    def __init__(self,*args):
        if not args:
            args = [0,7,2,1]
        self.args = args
        self.expressions_dict = self.__generate_expressions_dict(self.args)
        
    def __split_list(self,lst):
        n = len(lst)
        for i in range(1, n):
            for indices in combinations(range(1, n), i):
                yield tuple([lst[j:k] for j, k in zip((0,) + indices, indices + (n,))])

    def __evaluate_expression(self,expression):
        try:
            result = eval(expression)
            return result
        except Exception as e:
            print(f"Error evaluating {expression}: {e}")
            return None
        
    # Helper function to simplify expressions by removing unnecessary parentheses
    def __simplify_expression(self,expression):
        # Regular expression to match single numbers enclosed in parentheses
        single_num_in_parentheses = r'\(\s*(\d+)\s*\)'

        # Function to replace matched parentheses around single numbers with the number itself
        def remove_parentheses(match):
            return match.group(1)

        # Replace unnecessary parentheses using the regular expression and replacement function
        simplified_expression = re.sub(single_num_in_parentheses, remove_parentheses, expression)
        
        if not simplified_expression.startswith('(') or not simplified_expression.endswith(')'):
            simplified_expression = f'({simplified_expression})'
        
        return simplified_expression

    def __generate_expressions_dict(self,nums):
        expressions_dict = {}
        
        # 遍历所有可能的分组方式
        for groups in self.__split_list(nums):
            # 对于每种分组，生成所有可能的表达式
            for ops in ['+', '-', '*']:
                if ops == '*':
                    # 处理乘法情况
                    expr = '*'.join(f"({'+'.join(map(str, group))})" for group in groups)
                else:
                    # 处理加法和减法情况
                    expr = ops.join('+'.join(map(str, group)) for group in groups)

                # 如果是乘法并且有超过两个组，则需要添加外层括号
                if ops == '*' and len(groups) > 1:
                    expr = f"({expr})"
                    
                # 计算表达式的结果并添加到字典中
                result = self.__evaluate_expression(expr)
                if result is not None:
                    expressions_dict[result] = self.__simplify_expression(expr)
    
        return expressions_dict

    def Onani_generate_number(self,target=int("0D00",16)):
        
        if target in self.expressions_dict:
            return self.expressions_dict[target]

        # Sort keys by their values in descending order for efficiency
        sorted_keys = sorted(self.expressions_dict.keys(), reverse=True)

        def build_expression(target, used_keys=set()):
            # Base case: target is already in the expressions_dict
            if target in self.expressions_dict and target not in used_keys:
                return self.expressions_dict[target]
            
            # Try subtracting each key from the target and recurse
            for key in sorted_keys:
                if key not in used_keys and key <= target:
                    remainder = target - key
                    expression = self.expressions_dict[key]
                    sub_expression = build_expression(remainder, used_keys | {key})
                    if sub_expression is not None:
                        return f"({expression})+({sub_expression})"
                    
            # No expression could be built
            return None

        result = build_expression(target)
        return result

    