#!/usr/bin/env python3
import json
import sys
from jsonschema import Draft7Validator, ValidationError
from jsonschema.exceptions import SchemaError

def validate_json_schema(schema_file_path):
    """
    验证JSON Schema文件是否合法
    """
    try:
        # 读取JSON Schema文件
        with open(schema_file_path, 'r', encoding='utf-8') as f:
            schema = json.load(f)
        
        # 验证是否为合法的JSON Schema
        Draft7Validator.check_schema(schema)
        
        print(f"✅ {schema_file_path} 是一个合法的JSON Schema文件")
        
        # 输出Schema的基本信息
        print(f"\n📋 Schema信息:")
        print(f"   标题: {schema.get('title', 'N/A')}")
        print(f"   描述: {schema.get('description', 'N/A')}")
        print(f"   类型: {schema.get('type', 'N/A')}")
        print(f"   Schema版本: {schema.get('$schema', 'N/A')}")
        
        if 'properties' in schema:
            print(f"   属性数量: {len(schema['properties'])}")
            print(f"   必需字段: {schema.get('required', [])}")
        
        return True
        
    except json.JSONDecodeError as e:
        print(f"❌ JSON语法错误: {e}")
        return False
    except SchemaError as e:
        print(f"❌ JSON Schema格式错误: {e}")
        return False
    except FileNotFoundError:
        print(f"❌ 文件未找到: {schema_file_path}")
        return False
    except Exception as e:
        print(f"❌ 验证过程中发生错误: {e}")
        return False

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("用法: python3 validate_schema.py <schema_file_path>")
        sys.exit(1)
    
    schema_file = sys.argv[1]
    is_valid = validate_json_schema(schema_file)
    
    sys.exit(0 if is_valid else 1)