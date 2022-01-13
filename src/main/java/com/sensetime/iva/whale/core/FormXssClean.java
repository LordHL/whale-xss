/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sensetime.iva.whale.core;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

/**
 * @Description form 表单处理 xss
 * @Date 2021/12/24
 * @Created by helin
 */
@ControllerAdvice
public class FormXssClean {
	private final XssCleaner xssCleaner;

	public FormXssClean(XssCleaner xssCleaner){
		this.xssCleaner = xssCleaner;
	}
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// 处理前端传来的表单字符串
		binder.registerCustomEditor(String.class, new StringPropertiesEditor(xssCleaner));
	}

	public static class StringPropertiesEditor extends PropertyEditorSupport {
        private static final Logger log = LoggerFactory.getLogger(StringPropertiesEditor.class);
		private final XssCleaner xssCleaner;

		public StringPropertiesEditor(XssCleaner xssCleaner){
		    this.xssCleaner = xssCleaner;
        }
		@Override
		public String getAsText() {
			Object value = getValue();
			return value != null ? value.toString() :"";
		}

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (text == null) {
				setValue(null);
			} else if (XssHolder.isEnabled()) {
				String value = xssCleaner.clean(text);
				setValue(value);
				log.debug("Request parameter value:{} cleaned up by whale-xss, current value is:{}.", text, value);
			} else {
				setValue(text);
			}
		}
	}

}
