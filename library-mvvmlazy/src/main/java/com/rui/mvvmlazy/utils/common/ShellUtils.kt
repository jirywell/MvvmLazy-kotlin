/*
 * Copyright (C) 2018 jirui_zhao(jirui_zhao@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rui.mvvmlazy.utils.common

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

/**
 *
 * <pre>
 * desc   : ShellUtils
 * author : zjr
 * time   : 2018/4/28 上午12:42
</pre> *
 *
 * **Check root**
 *  * [ShellUtils.checkRootPermission]
 *
 *
 * **Execte command**
 *  * [ShellUtils.execCommand]
 *  * [ShellUtils.execCommand]
 *  * [ShellUtils.execCommand]
 *  * [ShellUtils.execCommand]
 *  * [ShellUtils.execCommand]
 *  * [ShellUtils.execCommand]
 *
 *
 */
class ShellUtils() {
    /**
     * result of command
     *
     *  * [CommandResult.result] means result of command, 0 means normal,
     * else means error, same to excute in linux shell
     *  * [CommandResult.successMsg] means success message of command
     * result
     *  * [CommandResult.errorMsg] means error message of command result
     *
     *
     * @author [Trinea](http://www.trinea.cn)
     * 2013-5-16
     */
    class CommandResult {
        /**
         * result of command *
         */
        val result: Int

        /**
         * success message of command result *
         */
        var successMsg: String? = null

        /**
         * error message of command result *
         */
        var errorMsg: String? = null

        constructor(result: Int) {
            this.result = result
        }

        constructor(result: Int, successMsg: String?, errorMsg: String?) {
            this.result = result
            this.successMsg = successMsg
            this.errorMsg = errorMsg
        }
    }

    companion object {
        const val COMMAND_SU = "su"
        const val COMMAND_SH = "sh"
        const val COMMAND_EXIT = "exit\n"
        const val COMMAND_LINE_END = "\n"

        /**
         * check whether has root permission
         *
         * @return
         */
        fun checkRootPermission(): Boolean {
            return execCommand("echo root", true, false).result == 0
        }

        /**
         * execute shell command, default return result msg
         *
         * @param command command
         * @param isRoot  whether need to run with root
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(command: String, isRoot: Boolean): CommandResult {
            return execCommand(arrayOf(command), isRoot, true)
        }

        /**
         * execute shell commands, default return result msg
         *
         * @param commands command list
         * @param isRoot   whether need to run with root
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(
            commands: List<String>,
            isRoot: Boolean
        ): CommandResult {
            return execCommand(
                commands.toTypedArray(),
                isRoot, true
            )
        }

        /**
         * execute shell command
         *
         * @param command         command
         * @param isRoot          whether need to run with root
         * @param isNeedResultMsg whether need result msg
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(
            command: String, isRoot: Boolean,
            isNeedResultMsg: Boolean
        ): CommandResult {
            return execCommand(arrayOf(command), isRoot, isNeedResultMsg)
        }

        /**
         * execute shell commands
         *
         * @param commands        command list
         * @param isRoot          whether need to run with root
         * @param isNeedResultMsg whether need result msg
         * @return
         * @see ShellUtils.execCommand
         */
        fun execCommand(
            commands: List<String>,
            isRoot: Boolean, isNeedResultMsg: Boolean
        ): CommandResult {
            return execCommand(
                commands.toTypedArray(),
                isRoot, isNeedResultMsg
            )
        }
        /**
         * execute shell commands
         *
         * @param commands        command array
         * @param isRoot          whether need to run with root
         * @param isNeedResultMsg whether need result msg
         * @return
         *  * if isNeedResultMsg is false, [CommandResult.successMsg]
         * is null and [CommandResult.errorMsg] is null.
         *  * if [CommandResult.result] is -1, there maybe some
         * excepiton.
         *
         */
        /**
         * execute shell commands, default return result msg
         *
         * @param commands command array
         * @param isRoot   whether need to run with root
         * @return
         * @see ShellUtils.execCommand
         */
        @JvmOverloads
        fun execCommand(
            commands: Array<String>?, isRoot: Boolean,
            isNeedResultMsg: Boolean = true
        ): CommandResult {
            var result = -1
            if (commands == null || commands.size == 0) {
                return CommandResult(result, null, null)
            }
            var process: Process? = null
            var successResult: BufferedReader? = null
            var errorResult: BufferedReader? = null
            var successMsg: StringBuilder? = null
            var errorMsg: StringBuilder? = null
            var os: DataOutputStream? = null
            try {
                process = Runtime.getRuntime().exec(
                    if (isRoot) COMMAND_SU else COMMAND_SH
                )
                os = DataOutputStream(process.outputStream)
                for (command in commands) {
                    if (command == null) {
                        continue
                    }

                    // donnot use os.writeBytes(commmand), avoid chinese charset
                    // error
                    os.write(command.toByteArray())
                    os.writeBytes(COMMAND_LINE_END)
                    os.flush()
                }
                os.writeBytes(COMMAND_EXIT)
                os.flush()
                result = process.waitFor()
                // get command result
                if (isNeedResultMsg) {
                    successMsg = StringBuilder()
                    errorMsg = StringBuilder()
                    successResult = BufferedReader(
                        InputStreamReader(
                            process.inputStream
                        )
                    )
                    errorResult = BufferedReader(
                        InputStreamReader(
                            process.errorStream
                        )
                    )
                    var s: String?
                    while (successResult.readLine().also { s = it } != null) {
                        successMsg.append(s)
                    }
                    while (errorResult.readLine().also { s = it } != null) {
                        errorMsg.append(s)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    os?.close()
                    successResult?.close()
                    errorResult?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                process?.destroy()
            }
            return CommandResult(result, successMsg?.toString(), errorMsg?.toString())
        }
    }

    /**
     * Don't let anyone instantiate this class.
     */
    init {
        throw Error("Do not need instantiate!")
    }
}